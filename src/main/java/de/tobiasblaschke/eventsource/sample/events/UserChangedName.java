package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;

import java.time.Instant;
import java.util.Optional;

public class UserChangedName extends AbstractUserEvent {
    final String surname;
    final String givenName;

    public UserChangedName(int userId, final String surname, final String givenName, Instant eventTimestamp, InconsistencyService inconsistencies) {
        super(userId, eventTimestamp, inconsistencies);
        this.surname = surname;
        this.givenName = givenName;
    }

    @Override
    public Optional<User> applyTo(Optional<User> previous) {
        if (! previous.isPresent()) {
            getInconsistencies().report(previous, this, "Trying to update a user, that does not exist.");
        }
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
