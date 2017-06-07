package de.tobiasblaschke.eventsource.scaffolding.impl;

import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.domain.Snapshot;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *  Keeps Events in Maps.
 *
 *  You'll need one store per payload type P.
 *
 *  @param <I> Id of the events payload
 *  @param <P> Type of the DTO attached to the event (like a User)
 */
public class EventStoreInMemory<I, P> implements EventStore<I, P> {
    private Map<I, List<Event<I, P>>> events;
    private Map<I, Snapshot<I, P>> snapshots;  // There's only one snapshot in this implementation
    private Class<P> payloadType;

    public EventStoreInMemory(final Class<P> payloadType) {
        events = new ConcurrentHashMap<>();
        snapshots = new ConcurrentHashMap<>();
        this.payloadType = payloadType;
    }

    @Override
    public List<Event<I, P>> getEventsFor(I id, Instant fromWhenOn) {
        return events.getOrDefault(id, Collections.emptyList()).stream()
                .filter(event -> event.getEventTimestamp().isAfter(fromWhenOn))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event<I, P>> getAllEvents(Instant fromWhenOn) {
        return events.values().stream()
                .flatMap(idEvents -> idEvents.stream())
                .sorted(Comparator.comparing(Event::getEventTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public void storeEvent(Event<I, P> event) {
        events.computeIfAbsent(event.getId(), id -> new ArrayList());
        events.compute(event.getId(), (key, events) -> {
            events.add(event);
            return events;
        });

    }

    @Override
    public Optional<Snapshot<I, P>> getSnapshotFor(I id, Instant atTime) {
        final Optional<Snapshot<I, P>> candidate = (snapshots.containsKey(id) ? Optional.of(snapshots.get(id)) : Optional.empty());

        if (candidate.isPresent() && candidate.get().getSnapshotTimestamp().isBefore(atTime)) {
            return candidate;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void storeSnapshot(Snapshot<I, P> snapshot) {
        snapshots.put(snapshot.getId(), snapshot);
    }

    @Override
    public Class<P> getPayloadType() {
        return payloadType;
    }
}
