package de.tobiasblaschke.eventsource.scaffolding.domain;

import java.time.Instant;
import java.util.Optional;

/**
 *  Represents the value of the payload (generate by replaying the Events up to the previous
 *  snapshot) at a given point in time.
 *
 *  @param <I> Id of the events payload
 *  @param <P> Type of the DTO attached to the event (like a User)
 */
public interface Snapshot<I, P> {
    I getId();
    Instant getSnapshotTimestamp();
    P getData();
}
