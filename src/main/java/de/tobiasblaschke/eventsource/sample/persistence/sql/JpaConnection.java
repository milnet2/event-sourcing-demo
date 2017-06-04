package de.tobiasblaschke.eventsource.sample.persistence.sql;

import de.tobiasblaschke.eventsource.sample.persistence.sql.repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaConnection {
    private UserRepository users;

    public JpaConnection() {
        this(
                Persistence.createEntityManagerFactory("userEvent").createEntityManager());
    }

    public JpaConnection(final EntityManager em) {
        users = new UserRepository(em);

    }

    public UserRepository getUsers() {
        return users;
    }
}
