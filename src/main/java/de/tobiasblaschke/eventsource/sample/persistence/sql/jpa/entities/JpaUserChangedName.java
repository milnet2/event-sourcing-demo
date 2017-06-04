package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import com.google.common.base.Preconditions;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.UserChangedName;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;

@ParametersAreNonnullByDefault
@Entity(name = "UserChangedName")
@DiscriminatorValue("CHANGED_NAME")
public class JpaUserChangedName extends AbstractJpaUserEvent {
    @Column(name = "given_name")
    private String givenName;

    @Column
    private String surname;

    public JpaUserChangedName() {
    }

    public JpaUserChangedName(int id, Instant eventTimestamp, String givenName, String surname) {
        super(id, eventTimestamp);
        this.givenName = givenName;
        this.surname = surname;
    }

    public JpaUserChangedName(User user, Instant eventTimestamp) {
        this(user.getUserId(), eventTimestamp, user.getGivenName(), user.getSurname());
    }

    public JpaUserChangedName(UserChangedName event) {
        this(event.getId(), event.getEventTimestamp(), event.getGivenName(), event.getSurname());
    }

    @Override
    public UserChangedName unbox() {
        Preconditions.checkNotNull(givenName);
        Preconditions.checkNotNull(surname);

        return new UserChangedName(id, surname, givenName, eventTimestamp);
    }
}
