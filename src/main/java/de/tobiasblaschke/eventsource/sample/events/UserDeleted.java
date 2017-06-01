package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.events.RevertEvent;

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

    public RevertEvent<Integer, User, UserDeleted> makeInverse(final EventStore<Integer, User> store, final Instant eventTimestamp) {
        return new RevertEvent<>(this, store, eventTimestamp);
    }

    public User getUser() {
        return user;
    }
}
