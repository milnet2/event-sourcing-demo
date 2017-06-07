package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.OrderedProduct;
import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class Bought implements Event<UUID, OrderedProduct> {
    final UUID id;
    final OrderedProduct orderedProduct;
    final Instant eventTimestamp;
    final transient InconsistencyService inconsistencies;

    public Bought(final User user, final Product product, final Instant eventTimestamp, final InconsistencyService inconsistencies) {
        id = UUID.randomUUID();
        orderedProduct = new OrderedProduct(user, product);
        this.eventTimestamp = eventTimestamp;
        this.inconsistencies = inconsistencies;
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
        if (previous.isPresent()) {
            inconsistencies.report(previous, this, "There was a second buy-event on the same ordered product");
        }
        return Optional.of(orderedProduct);
    }
}
