package de.tobiasblaschke.eventsource.scaffolding.events;

import de.tobiasblaschke.eventsource.scaffolding.EventStore;

import java.time.Instant;
import java.util.Optional;

/**
 *  An Event represents a change to some payload-data P it operates on,
 *
 *  @param <I> Id of the events payload
 *  @param <P> Type of the DTO attached to the event (like a User) *
 */
public interface Event<I, P> {
    I getId();
    Instant getEventTimestamp();
    Optional<P> applyTo(final Optional<P> previous);

    default RevertEvent<I, P, ? extends Event<I, P>> makeInverse(final EventStore<I, P> store, final Instant eventTimestamp) {
        return new RevertEvent<>(this, store, eventTimestamp);
    }

}
