package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.time.Instant;
import java.util.Optional;

public class PriceChanged implements Event<Integer, Product> {
    final int productId;
    final int newPriceInCents;
    final Instant eventTimestamp;
    private final transient InconsistencyService inconsistencies;

    public PriceChanged(int productId, int newPriceInCents, Instant eventTimestamp, InconsistencyService inconsistencies) {
        this.productId = productId;
        this.newPriceInCents = newPriceInCents;
        this.eventTimestamp = eventTimestamp;
        this.inconsistencies = inconsistencies;
    }

    @Override
    public Integer getId() {
        return productId;
    }

    @Override
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public Optional<Product> applyTo(Optional<Product> previous) {
        if (! previous.isPresent()) {
            inconsistencies.report(previous, this, "No product present, when changing it's price!");
        }

        return previous.map(
                product -> new Product(productId, product.getName(), newPriceInCents));
    }
}
