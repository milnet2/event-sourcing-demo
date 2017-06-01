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

public interface ApplicationConfiguration {
    enum DefaultApplicationConfiguration implements ApplicationConfiguration {
        INSTANCE;

        final ListenableEventStore dispatcher;
        final InvoiceStore invoices;
        final OrderStore orders;
        final EventStore<Integer, User> users;
        final EventStore<Integer, Product> products;
        final EventStore<Object, Object> deadLetter;

        final UserService userService;
        final InvoicingService invoiceService;

        DefaultApplicationConfiguration() {
            this.invoices = new InvoiceStoreInMemory();
            this.orders = new OrderStoreInMemory();
            this.users = new EventStoreInMemory<>(User.class);
            this.products = new EventStoreInMemory<>(Product.class);
            this.deadLetter = new EventStoreInMemory<>(Object.class);

            this.invoiceService = new InvoicingService(invoices, orders);
            this.userService = new UserService(users, orders);

            this.dispatcher = buildWiring(invoices, orders, users, products, deadLetter, userService, invoiceService);
        }

        public ListenableEventStore getDispatcher() {
            return dispatcher;
        }

        public InvoiceStore getInvoices() {
            return invoices;
        }

        public OrderStore getOrders() {
            return orders;
        }

        public EventStore<Integer, User> getUsers() {
            return users;
        }

        public EventStore<Integer, Product> getProducts() {
            return products;
        }

        public EventStore<Object, Object> getDeadLetter() {
            return deadLetter;
        }
    }

    // TODO: Super pretty... static would be even worse
    // I trait would be nice...
    default ListenableEventStore buildWiring(InvoiceStore invoices, OrderStore orders, EventStore<Integer, User> users,
                                            EventStore<Integer, Product> products, EventStore deadLetter,
                                            UserService userService, InvoicingService invoiceService) {
         return ListenableEventStore.builder()
                // Stores
                .wire(Bought.class, orders)
                .wire(InvoiceSent.class, invoices)
                .wire(PriceChanged.class, products)
                .wire(ProductAdded.class, products)
                .wire(AbstractUserEvent.class, users)

                 // Services
                 .wire(UserDeleted.class, userService)
                 .wire(UserChangedEmail.class, invoiceService)

                .deadLetter(deadLetter)
                .build();
    }

    ListenableEventStore getDispatcher();

    InvoiceStore getInvoices();

    OrderStore getOrders();

    EventStore<Integer, User> getUsers();

    EventStore<Integer, Product> getProducts();

    EventStore<Object, Object> getDeadLetter();
}
