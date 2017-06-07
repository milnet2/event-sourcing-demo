package de.tobiasblaschke.eventsource.scaffolding.impl;

import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.events.Snapshot;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  Replays a sequence of events in order to produce the value of a payload P at
 *  a given point in time.
 *
 *  @param <I> Id of the events payload
 *  @param <P> Type of the DTO attached to the event (like a User)
 */
public class EventSourceService<I, P> {
    private final EventStore<I, P> store;
    private final InconsistencyService inconsistencies;

    public EventSourceService(EventStore<I, P> store, InconsistencyService inconsistencies) {
        this.store = store;
        this.inconsistencies = inconsistencies;
    }

    public Optional<P> get(final I id) {
        return get(id, Instant.MAX);
    }

    public Optional<P> get(final I id, final Instant atTime) {
        final Optional<Snapshot<I, P>> snapshot = store.getSnapshotFor(id, atTime);
        final Instant afterSnapshot = snapshot.map(Snapshot::getSnapshotTimestamp).orElse(Instant.MIN);
        final List<Event<I, P>> events = store.getEventsFor(id, afterSnapshot);
        final Optional<P> initialData = snapshot.map(Snapshot::getData);

        return replay(initialData, events, atTime);
    }

    private Optional<P> replay(final Optional<P> initial, final List<Event<I, P>> log, final Instant atTime) {
        return leftFold(initial,
                log.stream()
                        .filter(entry -> entry.getEventTimestamp().compareTo(atTime) <= 0)
                        .sorted(Comparator.comparing(event -> event.getEventTimestamp())))
                ;
    }

    private Optional<P> leftFold(final Optional<P> initial, final Stream<Event<I, P>> events) {
        Optional<P> current = initial;
        for (Event<I, P> event : events.collect(Collectors.toList())) {
            current = event.applyTo(current);
        }
        return current;
    }

    public EventStore<I, P> getStore() {
        return store;
    }

    public InconsistencyService getInconsistencies() {
        return inconsistencies;
    }
}

