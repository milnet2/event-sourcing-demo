package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.Invoice;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class InvoiceSent implements Event<UUID, Invoice> {
    final UUID id;
    final Invoice invoice;
    final Instant eventTimestamp;
    private final transient InconsistencyService incosistencies;

    public InvoiceSent(UUID id, Invoice invoice, Instant eventTimestamp, InconsistencyService inconsistencies) {
        this.id = id;
        this.invoice = invoice;
        this.eventTimestamp = eventTimestamp;
        this.incosistencies = inconsistencies;
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
    public Optional<Invoice> applyTo(Optional<Invoice> previous) {
        return Optional.of(invoice);
    }
}
