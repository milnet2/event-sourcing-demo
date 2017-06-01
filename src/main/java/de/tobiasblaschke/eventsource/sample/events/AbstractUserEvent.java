package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.time.Instant;

public abstract class AbstractUserEvent implements Event<Integer, User> {
    final int userId;
    final Instant eventTimestamp;

    public AbstractUserEvent(int userId, Instant eventTimestamp) {
        this.userId = userId;
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public Integer getId() {
        return userId;
    }

    @Override
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }
}
