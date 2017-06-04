package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import de.tobiasblaschke.eventsource.scaffolding.events.Snapshot;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractJpaSnapshot<I extends Serializable, P> implements Snapshot<I, P> {
    @Id
    @Column(name = "snapshot_id", updatable = false, nullable = false, unique = true)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    protected UUID snapshotId;

    @Column(name = "payload_id", updatable = false, nullable = false, unique = false)
    protected I id;

    @Column(name = "snapshot_timestamp")
    protected Instant snapshotTimestamp;

    public AbstractJpaSnapshot(I id, Instant snapshotTimestamp) {
        this.id = id;
        this.snapshotTimestamp = snapshotTimestamp;
    }

    @Override
    public I getId() {
        return id;
    }

    @Override
    public Instant getSnapshotTimestamp() {
        return snapshotTimestamp;
    }
}
