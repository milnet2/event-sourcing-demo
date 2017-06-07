package de.tobiasblaschke.eventsource.scaffolding.events;

import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.domain.Inconsistency;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class InconsistencyDetected implements Event<UUID, Inconsistency> {
    final Inconsistency inconsistency;
    final Instant eventTimestamp;

    public InconsistencyDetected(Inconsistency inconsistency, Instant eventTimestamp) {
        this.inconsistency = inconsistency;
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public UUID getId() {
        return inconsistency.getInconsistencyId();
    }

    @Override
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public Optional<Inconsistency> applyTo(Optional<Inconsistency> previous) {
        return Optional.of(inconsistency);
    }

    @Override
    public RevertEvent<UUID, Inconsistency, ? extends Event<UUID, Inconsistency>> makeInverse(EventStore<UUID, Inconsistency> store, Instant eventTimestamp) {
        throw new UnsupportedOperationException("Cannot un-detect an inconsistency.");
    }
}
