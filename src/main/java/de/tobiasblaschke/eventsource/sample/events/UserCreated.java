package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;

import java.time.Instant;
import java.util.Optional;

public class UserCreated extends AbstractUserEvent {
    final User user;

    public UserCreated(User user, Instant eventTimestamp, InconsistencyService inconsistencies) {
        super(user.getUserId(), eventTimestamp, inconsistencies);
        this.user = user;
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        if (previous.isPresent()) {
            getInconsistencies().report(previous, this, "Trying to create a user, that already exists.");
        }
        return Optional.of(user);
    }

    public User getUser() {
        return user;
    }
}
