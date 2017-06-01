package de.tobiasblaschke.eventsource.scaffolding.events;

import java.time.Instant;
import java.util.Optional;

public interface Event<I, P> {
    I getId();
    Instant getEventTimestamp();
    Optional<P> applyTo(final Optional<P> previous);
}
