package de.tobiasblaschke.eventsource.sample;

import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.*;
import de.tobiasblaschke.eventsource.sample.persistence.InvoiceStore;
import de.tobiasblaschke.eventsource.sample.persistence.OrderStore;
import de.tobiasblaschke.eventsource.sample.persistence.inmemory.InvoiceStoreInMemory;
import de.tobiasblaschke.eventsource.sample.persistence.inmemory.OrderStoreInMemory;
import de.tobiasblaschke.eventsource.sample.service.InvoicingService;
import de.tobiasblaschke.eventsource.sample.service.UserService;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventStoreInMemory;
import de.tobiasblaschke.eventsource.scaffolding.impl.ListenableEventStore;

public enum ApplicationConfiguration {  // TODO: This ist not good at all - in a test we can end up with the wrong instances
    INSTANCE;

    final ListenableEventStore dispatcher;
    final InvoiceStoreInMemory invoices;
    final OrderStoreInMemory orders;
    final EventStoreInMemory<Integer, User> users;
    final EventStoreInMemory<Integer, Product> products;
    final EventStoreInMemory<Object, Object> deadLetter;

    final UserService userService;
    final InvoicingService invoiceService;

    ApplicationConfiguration() {
        this.invoices = new InvoiceStoreInMemory();
        this.orders = new OrderStoreInMemory();
        this.users = new EventStoreInMemory<>(User.class);
        this.products = new EventStoreInMemory<>(Product.class);
        this.deadLetter = new EventStoreInMemory<>(Object.class);

        this.invoiceService = new InvoicingService(invoices, orders);
        this.userService = new UserService(users, orders);

        this.dispatcher = buildWiring(invoices, orders, users, products, deadLetter, userService, invoiceService);
    }

    public ListenableEventStore buildWiring(InvoiceStore invoices, OrderStore orders, EventStore<Integer, User> users,
                                            EventStore<Integer, Product> products, EventStore deadLetter,
                                            UserService userService, InvoicingService invoiceService) {
         return ListenableEventStore.builder()
                // Stores
                .wire(Bought.class, orders)
                .wire(InvoiceSent.class, invoices)
                .wire(PriceChanged.class, products)
                .wire(ProductAdded.class, products)
                .wire(UserChangedName.class, users)
                .wire(UserCreated.class, users)
                .wire(UserDeleted.class, users)
                .wire(UserChangedEmail.class, users)

                 // Services
                 .wire(UserDeleted.class, userService)
                 .wire(UserChangedEmail.class, invoiceService)

                .deadLetter(deadLetter)
                .build();
    }

    public ListenableEventStore getDispatcher() {
        return dispatcher;
    }

    public InvoiceStoreInMemory getInvoices() {
        return invoices;
    }

    public OrderStoreInMemory getOrders() {
        return orders;
    }

    public EventStoreInMemory<Integer, User> getUsers() {
        return users;
    }

    public EventStoreInMemory<Integer, Product> getProducts() {
        return products;
    }

    public EventStoreInMemory<Object, Object> getDeadLetter() {
        return deadLetter;
    }
}
