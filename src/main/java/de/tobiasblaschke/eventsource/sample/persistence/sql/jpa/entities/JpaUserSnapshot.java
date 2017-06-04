package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.events.Snapshot;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.*;
import java.time.Instant;

@ParametersAreNonnullByDefault
@Entity(name = "user_snapshot")
public class JpaUserSnapshot extends AbstractJpaSnapshot<Integer, User> {
    @Column(name = "given_name")
    private String givenName;

    @Column
    private String surname;

    @Column
    private String email;

    protected JpaUserSnapshot() {
        super(null, null);
    }

    public JpaUserSnapshot(Integer id, Instant snapshotTimestamp, String givenName, String surname, String email) {
        super(id, snapshotTimestamp);
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
    }

    public JpaUserSnapshot(Snapshot<Integer, User> snapshot) {
        this(snapshot.getId(), snapshot.getSnapshotTimestamp(),
                snapshot.getData().getGivenName(),
                snapshot.getData().getSurname(),
                snapshot.getData().getGivenName());
    }

    @Override
    public User getData() {
        return new User(id, givenName, surname, email);
    }
}
