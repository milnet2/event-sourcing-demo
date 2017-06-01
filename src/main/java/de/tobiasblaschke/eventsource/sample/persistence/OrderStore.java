package de.tobiasblaschke.eventsource.sample.persistence;

import de.tobiasblaschke.eventsource.sample.domain.OrderedProduct;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderStore extends EventStore<UUID, OrderedProduct> {
    List<OrderedProduct> byUser(User user, Instant fromWhenOn);
    List<OrderedProduct> byUser(int userId, Instant fromWhenOn);
}
