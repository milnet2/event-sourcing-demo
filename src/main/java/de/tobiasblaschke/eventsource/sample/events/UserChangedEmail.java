package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;

import java.time.Instant;
import java.util.Optional;

public class UserChangedEmail extends AbstractUserEvent {
    final String email;

    public UserChangedEmail(int userId, final String email, Instant eventTimestamp) {
        super(userId, eventTimestamp);
        this.email = email;
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        assert previous.isPresent();
        return previous
                .map(prev ->
                        new User(prev.getUserId(), prev.getGivenName(), prev.getSurname(), email)); // TODO: There aught to be a better way!

    }
}
