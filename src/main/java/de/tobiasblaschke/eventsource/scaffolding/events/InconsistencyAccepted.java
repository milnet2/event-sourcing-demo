package de.tobiasblaschke.eventsource.scaffolding.events;

import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.domain.Inconsistency;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class InconsistencyAccepted implements Event<UUID, Inconsistency> {
    final UUID id;
    final Instant eventTimestamp;

    public InconsistencyAccepted(UUID inconsistencyId, Instant eventTimestamp) {
        this.id = inconsistencyId;
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public Optional<Inconsistency> applyTo(Optional<Inconsistency> previous) {
        return previous
                .map(p -> new Inconsistency(id, p.getDataBefore(), p.getWhenApplying(), p.getMessage(), true));
    }
}
