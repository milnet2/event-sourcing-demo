package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import de.tobiasblaschke.eventsource.sample.events.AbstractUserEvent;

import javax.annotation.Nonnull;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.Instant;

@Entity(name = "user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
public abstract class AbstractJpaUserEvent extends AbstractJpaEvent<Integer> {
    public AbstractJpaUserEvent() {
        super(null, null);
    }

    public AbstractJpaUserEvent(@Nonnull Integer id, @Nonnull Instant eventTimestamp) {
        super(id, eventTimestamp);
    }

    public abstract AbstractUserEvent unbox();
}
