package de.tobiasblaschke.eventsource.scaffolding.events;

import java.time.Instant;
import java.util.Optional;

public interface Snapshot<I, P> {
    I getId();
    Instant getSnapshotTimestamp();
    P getData();
}
