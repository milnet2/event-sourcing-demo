package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.UserDeleted;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;

@ParametersAreNonnullByDefault
@Entity(name = "UserDeleted")
@DiscriminatorValue("DELETED")
public class JpaUserDeleted extends AbstractJpaUserEvent {
    public JpaUserDeleted() {
    }

    public JpaUserDeleted(int id, Instant eventTimestamp) {
        super(id, eventTimestamp);
    }

    public JpaUserDeleted(User user, Instant eventTimestamp) {
        this(user.getUserId(), eventTimestamp);
    }

    public JpaUserDeleted(UserDeleted event) {
        this(event.getId(), event.getEventTimestamp());
    }

    @Override
    public UserDeleted unbox() {
        return new UserDeleted(id, eventTimestamp);
    }
}
