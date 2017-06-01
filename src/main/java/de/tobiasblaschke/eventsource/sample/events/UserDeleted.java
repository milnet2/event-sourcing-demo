package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;

import java.time.Instant;
import java.util.Optional;

public class UserDeleted extends AbstractUserEvent {
    final User user;

    public UserDeleted(User user, Instant eventTimestamp) {
        super(user.getUserId(), eventTimestamp);
        this.user = user;
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        return Optional.empty();
    }

    public UserCreated makeInverse() {
        return new UserCreated(user, getEventTimestamp());
    }

    public User getUser() {
        return user;
    }
}
