package de.tobiasblaschke.eventsource.sample.persistence.inmemory;

import de.tobiasblaschke.eventsource.sample.domain.Invoice;
import de.tobiasblaschke.eventsource.sample.domain.OrderedProduct;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.persistence.OrderStore;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.events.Snapshot;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventStoreInMemory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderStoreInMemory implements OrderStore {
    final EventStoreInMemory<UUID, OrderedProduct> delegate;

    public OrderStoreInMemory() {
        this.delegate = new EventStoreInMemory<>(OrderedProduct.class);
    }

    @Override
    public List<Event<UUID, OrderedProduct>> getEventsFor(UUID id, Instant fromWhenOn) {
        return delegate.getEventsFor(id, fromWhenOn);
    }

    @Override
    public List<Event<UUID, OrderedProduct>> getAllEvents(Instant fromWhenOn) {
        return delegate.getAllEvents(fromWhenOn);
    }

    @Override
    public List<OrderedProduct> byUser(User user, Instant fromWhenOn) {
        return byUser(user.getUserId(), fromWhenOn);
    }

    @Override
    public List<OrderedProduct> byUser(int userId, Instant fromWhenOn) {
        return getAllEvents(fromWhenOn).stream()
                .map(event -> event.applyTo(Optional.empty()).get())    // TODO: This will only work if invoices don't change
                .filter(ordered -> ordered.getUser().getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Snapshot<UUID, OrderedProduct>> getSnapshotFor(UUID id, Instant atTime) {
        return delegate.getSnapshotFor(id, atTime);
    }

    @Override
    public void storeEvent(Event<UUID, OrderedProduct> event) {
        delegate.storeEvent(event);
    }

    @Override
    public void storeSnapshot(Snapshot<UUID, OrderedProduct> snapshot) {
        delegate.storeSnapshot(snapshot);
    }

    @Override
    public Class<OrderedProduct> getPayloadType() {
        return OrderedProduct.class;
    }
}
