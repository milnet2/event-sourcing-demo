package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.events.RevertEvent;

import java.time.Instant;
import java.util.Optional;

public class UserDeleted extends AbstractUserEvent {

    public UserDeleted(int userId, Instant eventTimestamp) {
        super(userId, eventTimestamp);
    }

    public UserDeleted(User user, Instant eventTimestamp) {
        this(user.getUserId(), eventTimestamp);
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        return Optional.empty();
    }

    public RevertEvent<Integer, User, UserDeleted> makeInverse(final EventStore<Integer, User> store, final Instant eventTimestamp) {
        return new RevertEvent<>(this, store, eventTimestamp);
    }
}
