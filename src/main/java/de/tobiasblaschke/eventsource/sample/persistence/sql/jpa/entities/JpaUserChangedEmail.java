package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import com.google.common.base.Preconditions;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.UserChangedEmail;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;

@ParametersAreNonnullByDefault
@Entity(name = "UserChangedEmail")
@DiscriminatorValue("CHANGED_MAIL")
public class JpaUserChangedEmail extends AbstractJpaUserEvent {
    @Column
    private String email;

    public JpaUserChangedEmail() {
    }

    public JpaUserChangedEmail(int id, Instant eventTimestamp, String email) {
        super(id, eventTimestamp);
        this.email = email;
    }

    public JpaUserChangedEmail(User user, Instant eventTimestamp) {
        this(user.getUserId(), eventTimestamp, user.getEmail());
    }

    public JpaUserChangedEmail(UserChangedEmail event) {
        this(event.getId(), event.getEventTimestamp(), event.getEmail());
    }

    @Override
    public UserChangedEmail unbox() {
        Preconditions.checkNotNull(email);

        return new UserChangedEmail(id, email, eventTimestamp);
    }
}
