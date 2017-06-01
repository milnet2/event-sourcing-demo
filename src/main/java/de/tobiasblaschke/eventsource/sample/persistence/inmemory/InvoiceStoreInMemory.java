package de.tobiasblaschke.eventsource.sample.persistence.inmemory;

import de.tobiasblaschke.eventsource.sample.domain.Invoice;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.persistence.InvoiceStore;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.events.Snapshot;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventStoreInMemory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InvoiceStoreInMemory implements InvoiceStore {
    final EventStoreInMemory<UUID, Invoice> delegate;

    public InvoiceStoreInMemory() {
        this.delegate = new EventStoreInMemory<>(Invoice.class);
    }

    @Override
    public List<Event<UUID, Invoice>> getEventsFor(UUID id, Instant fromWhenOn) {
        return delegate.getEventsFor(id, fromWhenOn);
    }

    @Override
    public List<Event<UUID, Invoice>> getAllEvents(Instant fromWhenOn) {
        return delegate.getAllEvents(fromWhenOn);
    }

    @Override
    public List<Invoice> byUser(User user, Instant fromWhenOn) {
        return byUser(user.getUserId(), fromWhenOn);
    }

    @Override
    public List<Invoice> byUser(int userId, Instant fromWhenOn) {
        return getAllEvents(fromWhenOn).stream()
                .map(event -> event.applyTo(Optional.empty()).get())    // TODO: This will only work if invoices don't change
                .filter(invoice -> invoice.getUser().getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Snapshot<UUID, Invoice>> getSnapshotFor(UUID id, Instant atTime) {
        return delegate.getSnapshotFor(id, atTime);
    }

    @Override
    public void storeEvent(Event<UUID, Invoice> event) {
        delegate.storeEvent(event);
    }

    @Override
    public void storeSnapshot(Snapshot<UUID, Invoice> snapshot) {
        delegate.storeSnapshot(snapshot);
    }

    @Override
    public Class<Invoice> getPayloadType() {
        return Invoice.class;
    }
}
