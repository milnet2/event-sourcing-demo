package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.RevertEvent;

import java.time.Instant;
import java.util.Optional;

public class UserDeleted extends AbstractUserEvent {

    public UserDeleted(int userId, Instant eventTimestamp, InconsistencyService inconsistencies) {
        super(userId, eventTimestamp, inconsistencies);
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        if (! previous.isPresent()) {
            inconsistencies.report(previous, this, "Trying to delete a user, that does not exist");
        }
        return Optional.empty();
    }

    public RevertEvent<Integer, User, UserDeleted> makeInverse(final EventStore<Integer, User> store, final Instant eventTimestamp) {
        return new RevertEvent<>(this, store, eventTimestamp);
    }
}
