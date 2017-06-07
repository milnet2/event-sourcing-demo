package de.tobiasblaschke.eventsource.test;

import de.tobiasblaschke.eventsource.sample.ApplicationConfiguration;
import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.EventFactory;
import de.tobiasblaschke.eventsource.sample.persistence.inmemory.InvoiceStoreInMemory;
import de.tobiasblaschke.eventsource.sample.persistence.inmemory.OrderStoreInMemory;
import de.tobiasblaschke.eventsource.sample.service.InvoicingService;
import de.tobiasblaschke.eventsource.scaffolding.impl.PermissiveInconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.impl.ThrowingInconsistencyService;
import de.tobiasblaschke.eventsource.sample.service.UserService;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventStoreInMemory;
import de.tobiasblaschke.eventsource.scaffolding.impl.ListenableEventStore;

import java.time.Instant;
import java.util.Arrays;
import java.util.function.Function;

public class TestBase {
    protected static class State implements ApplicationConfiguration {
        final InconsistencyService inconsistencies;
        final EventFactory eventFactory;

        final ListenableEventStore dispatcher;
        final InvoiceStoreInMemory invoices;
        final OrderStoreInMemory orders;
        final EventStore<Integer, User> users;
        final EventStoreInMemory<Integer, Product> products;
        final EventStoreInMemory<Object, Object> deadLetter;

        private final InvoicingService invoiceService;
        private final UserService userService;

        public State() {
            this(new ThrowingInconsistencyService());
        }

        public State(InconsistencyService inconsistencies) {
            this.inconsistencies = inconsistencies;
            this.eventFactory = new EventFactory(inconsistencies);

            this.invoices = new InvoiceStoreInMemory();
            this.orders = new OrderStoreInMemory();
            this.users = new EventStoreInMemory<>(User.class);
            this.products = new EventStoreInMemory<>(Product.class);
            this.deadLetter = new EventStoreInMemory<>(Object.class);

            this.invoiceService = new InvoicingService(invoices, orders, inconsistencies);
            this.userService = new UserService(users, orders, inconsistencies);

            this.dispatcher = ApplicationConfiguration.buildWiring(
                    invoices, orders, users, products, deadLetter,
                    userService, invoiceService);
        }

        public InvoiceStoreInMemory getInvoices() {
            return invoices;
        }

        public OrderStoreInMemory getOrders() {
            return orders;
        }

        public EventStore<Integer, User> getUsers() {
            return users;
        }

        public EventStoreInMemory<Integer, Product> getProducts() {
            return products;
        }

        public ListenableEventStore getDispatcher() {
            return dispatcher;
        }

        public EventStoreInMemory<Object, java.lang.Object> getDeadLetter() {
            return deadLetter;
        }

        @Override
        public InconsistencyService getInconsistencies() {
            return inconsistencies;
        }

        public EventFactory getEventFactory() {
            return eventFactory;
        }
    }

    protected State replay(Function<EventFactory, Event>... events) {
        return replay(new State(), events);
    }

    protected State replay(State state, Function<EventFactory, Event>... events) {
        Arrays.stream(events)
                .forEach(event -> replaySingle(state, event));
        return state;
    }

    protected State replaySingle(State state, Function<EventFactory, Event> event) {
        state.getDispatcher()
                .storeEvent(event.apply(state.getEventFactory()));

        if (! state.getDeadLetter().getAllEvents(Instant.MIN).isEmpty()) {
            throw new IllegalStateException("DeadLetter received events! Check your wiring. Event was: " +
                    state.getDeadLetter().getAllEvents(Instant.MIN));
        }

        return state;
    }
}
