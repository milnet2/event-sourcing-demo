package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.OrderedProduct;
import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class Bought implements Event<UUID, OrderedProduct> {
    final UUID id;
    final OrderedProduct orderedProduct;
    final Instant eventTimestamp;

    public Bought(final User user, final Product product, final Instant eventTimestamp) {
        id = UUID.randomUUID();
        orderedProduct = new OrderedProduct(user, product);
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public Optional<OrderedProduct> applyTo(Optional<OrderedProduct> previous) {
        assert ! previous.isPresent();
        return Optional.of(orderedProduct);
    }
}
