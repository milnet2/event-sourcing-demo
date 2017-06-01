package de.tobiasblaschke.eventsource.scaffolding;

import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.events.Snapshot;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventStore<I, P> {
    List<Event<I, P>> getEventsFor(final I id, final Instant fromWhenOn);
    Optional<Snapshot<I, P>> getSnapshotFor(final I id, final Instant atTime);
    default Optional<Snapshot<I, P>> getSnapshotFor(final I id) {
        return getSnapshotFor(id, Instant.MAX);
    }

    List<Event<I, P>> getAllEvents(Instant fromWhenOn);

    void storeEvent(final Event<I, P> event);
    void storeSnapshot(final Snapshot<I, P> snapshot);

    Class<P> getPayloadType();
}
