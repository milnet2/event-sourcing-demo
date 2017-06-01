package de.tobiasblaschke.eventsource.sample.service;

import de.tobiasblaschke.eventsource.sample.domain.OrderedProduct;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.UserDeleted;
import de.tobiasblaschke.eventsource.sample.persistence.OrderStore;
import de.tobiasblaschke.eventsource.scaffolding.EventListener;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventSourceService;

import java.time.Instant;
import java.util.List;

public class UserService extends EventSourceService<Integer, User> implements EventListener<UserDeleted> {
    private final OrderStore orders;

    public UserService(EventStore<Integer, User> userStore, OrderStore orders) {
        super(userStore);
        this.orders = orders;
    }

    @Override
    public void onEvent(UserDeleted event) {
        final List<OrderedProduct> usersInvoices = orders.byUser(event.getId(), Instant.MIN);
        if (! usersInvoices.isEmpty()) {
            getStore().storeEvent(event.makeInverse(getStore(), Instant.now()));
        }
    }
}
