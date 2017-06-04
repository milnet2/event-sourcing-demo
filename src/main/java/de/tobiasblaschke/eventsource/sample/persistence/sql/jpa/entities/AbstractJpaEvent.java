package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractJpaEvent<I extends Serializable> {
    @Id
    @Column(name = "event_id", updatable = false, nullable = false, unique = true)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    protected UUID eventId;

    @Column(name = "payload_id", updatable = false, nullable = false, unique = false)
    protected I id;

    @Column(name = "event_timestamp", updatable = false, nullable = false)
    protected Instant eventTimestamp;

    public AbstractJpaEvent(I id, Instant eventTimestamp) {
        this.id = id;
        this.eventTimestamp = eventTimestamp;
    }
}
