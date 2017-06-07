package de.tobiasblaschke.eventsource.sample.persistence.sql;

import de.tobiasblaschke.eventsource.sample.events.EventFactory;
import de.tobiasblaschke.eventsource.sample.persistence.sql.repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class JpaConnection {
    private UserRepository users;

    public JpaConnection(final EventFactory factory) {
        this(
                Persistence.createEntityManagerFactory("userEvent").createEntityManager(),
                factory);
    }

    public JpaConnection(final EntityManager em, final EventFactory factory) {
        users = new UserRepository(em, factory);

    }

    public UserRepository getUsers() {
        return users;
    }
}
