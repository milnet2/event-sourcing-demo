package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import com.google.common.base.Preconditions;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.EventFactory;
import de.tobiasblaschke.eventsource.sample.events.UserCreated;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;

@ParametersAreNonnullByDefault
@Entity(name = "UserCreated")
@DiscriminatorValue("CREATED")
public class JpaUserCreated extends AbstractJpaUserEvent {
    @Column(name = "given_name")
    private String givenName;

    @Column
    private String surname;

    @Column
    private String email;

    public JpaUserCreated() {
    }

    public JpaUserCreated(int id, Instant eventTimestamp, String givenName, String surname, String email) {
        super(id, eventTimestamp);
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
    }

    public JpaUserCreated(User user, Instant eventTimestamp) {
        this(user.getUserId(), eventTimestamp, user.getGivenName(), user.getSurname(), user.getEmail());
    }

    public JpaUserCreated(UserCreated event) {
        this(event.getUser(), event.getEventTimestamp());
    }

    @Override
    public UserCreated unbox(EventFactory factory) {
        Preconditions.checkNotNull(givenName);
        Preconditions.checkNotNull(surname);
        Preconditions.checkNotNull(email);

        final User user = new User(id, givenName, surname, email);
        return factory.userCreated(user, eventTimestamp);
    }
}
