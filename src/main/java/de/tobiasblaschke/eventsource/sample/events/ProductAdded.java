package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.time.Instant;
import java.util.Optional;

public class ProductAdded implements Event<Integer, Product> {
    private final Product product;
    private final Instant eventTimestamp;
    private final transient InconsistencyService inconsistencies;

    public ProductAdded(Product product, Instant eventTimestamp, InconsistencyService inconsistencies) {
        this.product = product;
        this.eventTimestamp = eventTimestamp;
        this.inconsistencies = inconsistencies;
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
        if (previous.isPresent()) {
            inconsistencies.report(previous, this, "Product already present when trying to add a new one. Overwriting...");
        }
        return Optional.of(product);
    }
}
