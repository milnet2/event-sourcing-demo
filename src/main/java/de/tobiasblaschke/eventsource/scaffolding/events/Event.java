package de.tobiasblaschke.eventsource.scaffolding.events;

import de.tobiasblaschke.eventsource.scaffolding.EventStore;

import java.time.Instant;
import java.util.Optional;

public interface Event<I, P> {
    I getId();
    Instant getEventTimestamp();
    Optional<P> applyTo(final Optional<P> previous);

    default RevertEvent<I, P, ? extends Event<I, P>> makeInverse(final EventStore<I, P> store, final Instant eventTimestamp) {
        return new RevertEvent<>(this, store, eventTimestamp);
    }

}
