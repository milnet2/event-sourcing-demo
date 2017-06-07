package de.tobiasblaschke.eventsource.sample.persistence.sql.repositories;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.*;
import de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities.*;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.domain.Snapshot;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class UserRepository implements EventStore<Integer, User> {
    private final EntityManager em;
    private final EventFactory eventFactory;

    public UserRepository(EntityManager em, EventFactory eventFactory) {
        this.em = em;
        this.eventFactory = eventFactory;
    }

    @Override
    public List<Event<Integer, User>> getEventsFor(Integer id, Instant fromWhenOn) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<AbstractJpaUserEvent> querySkeleton = cb
                .createQuery(AbstractJpaUserEvent.class);
        final Root<AbstractJpaUserEvent> root = querySkeleton.from(AbstractJpaUserEvent.class);

        final ParameterExpression<Integer> payloadId = cb.parameter(Integer.class, "id");
        final ParameterExpression<Instant> eventTimestamp = cb.parameter(Instant.class, "event_timestamp");

        final CriteriaQuery<AbstractJpaUserEvent> query = querySkeleton
                .select(root)
                .where(cb.and(
                        cb.equal(root.get("id"), payloadId)//,
                //        cb.greaterThanOrEqualTo(root.get("eventTimestamp"), eventTimestamp)
                ));

        List<AbstractJpaUserEvent> boxedResults = em.createQuery(query)
                .setParameter("id", id)
//                .setParameter("event_timestamp", fromWhenOn)
                .getResultList();
        return toEventList(boxedResults);
    }

    @Override
    public List<Event<Integer, User>> getAllEvents(Instant fromWhenOn) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<AbstractJpaUserEvent> querySkeleton = cb
                .createQuery(AbstractJpaUserEvent.class);
        final Root<AbstractJpaUserEvent> root = querySkeleton.from(AbstractJpaUserEvent.class);

        final ParameterExpression<Instant> eventTimestamp = cb.parameter(Instant.class, "event_timestamp");

        final CriteriaQuery<AbstractJpaUserEvent> query = querySkeleton
                .select(root)
                .where(cb.and(
                        cb.greaterThanOrEqualTo(root.get("eventTimestamp"), eventTimestamp)));

        List<AbstractJpaUserEvent> boxedResults = em.createQuery(query)
                .setParameter("event_timestamp", fromWhenOn)
                .getResultList();
        return toEventList(boxedResults);
    }

    private List<Event<Integer, User>> toEventList(final @Nullable List<? extends AbstractJpaUserEvent> boxedResults) {
        if (boxedResults == null) {
            return Collections.emptyList();
        } else {
            return boxedResults.stream()
                    .map(abstractJpaUserEvent -> abstractJpaUserEvent.unbox(eventFactory))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Optional<Snapshot<Integer, User>> getSnapshotFor(Integer id, Instant atTime) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<JpaUserSnapshot> querySkeleton = cb
                .createQuery(JpaUserSnapshot.class);
        final Root<JpaUserSnapshot> root = querySkeleton.from(JpaUserSnapshot.class);

        final ParameterExpression<Instant> snapshotTimestamp = cb.parameter(Instant.class, "snapshot_timestamp");

        final CriteriaQuery<JpaUserSnapshot> query = querySkeleton
                .select(root)
                .where(cb.and(
                        cb.lessThanOrEqualTo(root.get("snapshotTimestamp"), snapshotTimestamp)))
                //.having(cb.max((Expression<Number>) snapshotTimestamp))       // TODO
                ;

        return Optional.ofNullable(
                    em.createQuery(query)
                    .setParameter("snapshot_timestamp", atTime)
                    .getResultList())
                .flatMap(list -> list.stream()
                    .max(Comparator.comparing(Snapshot::getSnapshotTimestamp))) // TODO: Handle in query
                    .map(identity -> identity);
    }



    @Override
    public void storeEvent(Event<Integer, User> event) {
        if (event instanceof UserDeleted) {
            em.getTransaction().begin();
            em.persist(new JpaUserDeleted((UserDeleted) event));
            em.getTransaction().commit();
        } else if (event instanceof UserCreated) {
            em.getTransaction().begin();
            em.persist(new JpaUserCreated((UserCreated) event));
            em.getTransaction().commit();
        } else if (event instanceof UserChangedName) {
            em.getTransaction().begin();
            em.persist(new JpaUserChangedName((UserChangedName) event));
            em.getTransaction().commit();
        } else if (event instanceof UserChangedEmail) {
            em.getTransaction().begin();
            em.persist(new JpaUserChangedEmail((UserChangedEmail) event));
            em.getTransaction().commit();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void storeSnapshot(Snapshot<Integer, User> snapshot) {
        em.getTransaction().begin();
        em.persist(new JpaUserSnapshot(snapshot));
        em.getTransaction().commit();
    }

    @Override
    public Class<User> getPayloadType() {
        return User.class;
    }
}
