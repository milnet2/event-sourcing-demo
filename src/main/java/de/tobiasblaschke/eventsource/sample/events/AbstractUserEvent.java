package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.time.Instant;

public abstract class AbstractUserEvent implements Event<Integer, User> {
    final int userId;
    final Instant eventTimestamp;
    final transient InconsistencyService inconsistencies;

    public AbstractUserEvent(int userId, Instant eventTimestamp, InconsistencyService inconsistencies) {
        this.userId = userId;
        this.eventTimestamp = eventTimestamp;
        this.inconsistencies = inconsistencies;
    }

    @Override
    public Integer getId() {
        return userId;
    }

    @Override
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    protected InconsistencyService getInconsistencies() {
        return inconsistencies;
    }
}
