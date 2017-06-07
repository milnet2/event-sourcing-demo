package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;

import java.time.Instant;
import java.util.Optional;

public class UserChangedEmail extends AbstractUserEvent {
    final String email;

    public UserChangedEmail(int userId, final String email, Instant eventTimestamp, InconsistencyService inconsistencies) {
        super(userId, eventTimestamp, inconsistencies);
        this.email = email;
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        if (! previous.isPresent()) {
            getInconsistencies().report(previous, this, "Trying to update a user, that does not exist.");
        }
        return previous
                .map(prev ->
                        new User(prev.getUserId(), prev.getGivenName(), prev.getSurname(), email)); // TODO: There aught to be a better way!

    }

    public String getEmail() {
        return email;
    }
}
