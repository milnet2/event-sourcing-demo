package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;

import java.time.Instant;
import java.util.Optional;

public class UserCreated extends AbstractUserEvent {
    final User user;

    public UserCreated(User user, Instant eventTimestamp) {
        super(user.getUserId(), eventTimestamp);
        this.user = user;
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        assert ! previous.isPresent();
        return Optional.of(user);
    }

    public User getUser() {
        return user;
    }
}
