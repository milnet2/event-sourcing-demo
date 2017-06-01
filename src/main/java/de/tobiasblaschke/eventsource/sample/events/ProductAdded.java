package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.time.Instant;
import java.util.Optional;

public class ProductAdded implements Event<Integer, Product> {
    final Product product;
    final Instant eventTimestamp;

    public ProductAdded(Product product, Instant eventTimestamp) {
        this.product = product;
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public Integer getId() {
        return product.getId();
    }

    @Override
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public Optional<Product> applyTo(Optional<Product> previous) {
        return Optional.of(product);
    }
}
