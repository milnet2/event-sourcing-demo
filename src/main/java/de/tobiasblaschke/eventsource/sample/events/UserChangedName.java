package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;

import java.time.Instant;
import java.util.Optional;

public class UserChangedName extends AbstractUserEvent {
    final String surname;
    final String givenName;

    public UserChangedName(int userId, final String surname, final String givenName, Instant eventTimestamp) {
        super(userId, eventTimestamp);
        this.surname = surname;
        this.givenName = givenName;
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        assert previous.isPresent();
        return previous
                .map(prev ->
                    new User(prev.getUserId(), givenName, surname, prev.getEmail()));   // TODO: There aught to be a better way

    }

    public String getSurname() {
        return surname;
    }

    public String getGivenName() {
        return givenName;
    }
}
