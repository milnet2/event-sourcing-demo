package de.tobiasblaschke.eventsource.scaffolding.events;

import de.tobiasblaschke.eventsource.scaffolding.EventStore;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 *  An Event that wraps an existing event to indicate, that its changes should be undone
 *
 *  @param <E> original event, that should be reverted
 *  @param <I> Id of the events payload
 *  @param <P> Type of the DTO attached to the event (like a User)
 */
public class RevertEvent<I, P, E extends Event<I, P>> implements Event<I, P> {
    private final E toBeReverted;
    private final EventStore<I, P> store;
    private final Instant eventTimestamp;

    public RevertEvent(E toBeReverted, final EventStore<I, P> store, final Instant eventTimestamp) {
        this.toBeReverted = toBeReverted;
        this.store = store;
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public I getId() {
        return toBeReverted.getId();
    }

    @Override
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public Optional<P> applyTo(Optional<P> previous) {
        final List<Event<I, P>> history = store.getEventsFor(getId(), Instant.MIN); // TODO: Complete log may be a bit much
        Optional<P> data = Optional.empty();
        for (Event<I, P> event : history) {
            if (event == this) {
                throw new IllegalStateException("Can't revert an event before it happened");
            } else if (event == toBeReverted) {
                return data;
            } else {
                data = event.applyTo(data);
            }
        }
        throw new IllegalStateException("Unreachable");
    }
}
