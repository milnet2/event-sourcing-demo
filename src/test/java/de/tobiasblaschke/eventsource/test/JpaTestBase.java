package de.tobiasblaschke.eventsource.test;

import de.tobiasblaschke.eventsource.sample.ApplicationConfiguration;
import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.persistence.inmemory.InvoiceStoreInMemory;
import de.tobiasblaschke.eventsource.sample.persistence.inmemory.OrderStoreInMemory;
import de.tobiasblaschke.eventsource.sample.persistence.sql.JpaConnection;
import de.tobiasblaschke.eventsource.sample.service.InvoicingService;
import de.tobiasblaschke.eventsource.sample.service.UserService;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventStoreInMemory;
import de.tobiasblaschke.eventsource.scaffolding.impl.ListenableEventStore;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.ITable;
import org.dbunit.util.TableFormatter;
import org.h2.tools.RunScript;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

public abstract class JpaTestBase implements ApplicationConfiguration {
    protected static EntityManagerFactory emf;
    protected static EntityManager em;
    protected static JdbcDatabaseTester dbUnit;
    protected static JpaConnection jpa;

    protected static ListenableEventStore dispatcher;
    protected static InvoiceStoreInMemory invoices;
    protected static OrderStoreInMemory orders;
    protected static EventStore<Integer, User> users;
    protected static EventStoreInMemory<Integer, Product> products;
    protected static EventStoreInMemory<Object, Object> deadLetter;

    protected static InvoicingService invoiceService;
    protected static UserService userService;

    @BeforeClass
    public static void init() throws Exception {
        Class.forName("org.h2.Driver");
        emf = Persistence.createEntityManagerFactory("mnf-pu-test");
        em = emf.createEntityManager();
        dbUnit = new JdbcDatabaseTester("org.h2.Driver", "jdbc:h2:mem:test", "", "");

        jpa = new JpaConnection(em);

        invoices = new InvoiceStoreInMemory();
        orders = new OrderStoreInMemory();
        users = jpa.getUsers();
        products = new EventStoreInMemory<>(Product.class);
        deadLetter = new EventStoreInMemory<>(Object.class);

        invoiceService = new InvoicingService(invoices, orders);
        userService = new UserService(users, orders);

        dispatcher = ApplicationConfiguration.buildWiring(
                invoices, orders, users, products, deadLetter,
                userService, invoiceService);
    }

    @Before
    public void initializeDatabase(){
        Session session = em.unwrap(Session.class);
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                try {
                    File script = new File(getClass().getResource("/schema.sql").getFile());
                    RunScript.execute(connection, new FileReader(script));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("could not initialize with script");
                }
            }
        });
    }

    public void printTable(String tableName) throws Exception {
        final ITable data = dbUnit.getConnection().createQueryTable("dump", "SELECT * from " + tableName);
        final TableFormatter formatter = new TableFormatter();
        System.out.println(formatter.format(data));
    }

    @AfterClass
    public static void tearDown(){
        if (em != null) {
            em.clear();
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    protected void replay(Event... events) {
        Arrays.stream(events)
                .forEach(event -> replaySingle(event));
    }

    protected void replaySingle(Event event) {
        getDispatcher()
                .storeEvent(event);

        if (! getDeadLetter().getAllEvents(Instant.MIN).isEmpty()) {
            throw new IllegalStateException("DeadLetter received events! Check your wiring. Event was: " +
                    getDeadLetter().getAllEvents(Instant.MIN));
        }
    }


    public JpaConnection getJpa() {
        return jpa;
    }

    @Override
    public ListenableEventStore getDispatcher() {
        return dispatcher;
    }

    @Override
    public InvoiceStoreInMemory getInvoices() {
        return invoices;
    }

    @Override
    public OrderStoreInMemory getOrders() {
        return orders;
    }

    @Override
    public EventStore<Integer, User> getUsers() {
        return users;
    }

    @Override
    public EventStoreInMemory<Integer, Product> getProducts() {
        return products;
    }

    @Override
    public EventStoreInMemory<Object, Object> getDeadLetter() {
        return deadLetter;
    }

    public InvoicingService getInvoiceService() {
        return invoiceService;
    }

    public UserService getUserService() {
        return userService;
    }
}
