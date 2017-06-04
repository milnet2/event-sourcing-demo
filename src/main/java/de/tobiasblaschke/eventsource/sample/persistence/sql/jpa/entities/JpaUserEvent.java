package de.tobiasblaschke.eventsource.sample.persistence.sql.jpa.entities;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Deprecated
@Entity
public class JpaUserEvent {
    private enum EventType {
        USER_CREATED(UserCreated.class, jpaUser -> new UserCreated(jpaUser.getUserChanges(), jpaUser.eventTimestamp)),
        USER_DELETED(UserDeleted.class, jpaUser -> new UserDeleted(jpaUser.getUserChanges(), jpaUser.eventTimestamp)),
        USER_CHANGED_NAME(UserChangedName.class, jpaUserEvent -> new UserChangedName(jpaUserEvent.userId, jpaUserEvent.surname, jpaUserEvent.givenName, jpaUserEvent.eventTimestamp)),
        USER_CHANGED_EMAIL(UserChangedEmail.class, jpaUserEvent -> new UserChangedEmail(jpaUserEvent.userId, jpaUserEvent.email, jpaUserEvent.eventTimestamp));

        private final Class<? extends AbstractUserEvent> eventType;
        private static final Map<Class<? extends AbstractUserEvent>, EventType> byClass = Arrays.stream(values())
                .collect(Collectors.toMap(item -> item.getEventType(), item -> item));
        private final Function<JpaUserEvent, ? extends AbstractUserEvent> mapper;

        <T extends AbstractUserEvent> EventType(final Class<T> eventType, Function<JpaUserEvent, T> mapper) {
            this.eventType = eventType;
            this.mapper = mapper;
        }

        public static EventType byClass(final Class<? extends AbstractUserEvent> cls) {
            return byClass.get(cls);
        }

        public Class<? extends AbstractUserEvent> getEventType() {
            return eventType;
        }

        public <T extends AbstractUserEvent> T map(final JpaUserEvent jpaUserEvent) {
            return (T) mapper.apply(jpaUserEvent);
        }
    }

    private EventType eventType;
    private Instant eventTimestamp;

    @Id
    private int userId;
    private String givenName;
    private String surname;
    private String email;

    protected JpaUserEvent() {

    }

    public JpaUserEvent(EventType eventType, Instant eventTimestamp, int userId, String givenName, String surname, String email) {
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
        this.userId = userId;
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
    }

    public JpaUserEvent(final AbstractUserEvent event) {
        this.eventType = EventType.byClass(event.getClass());
        this.eventTimestamp = event.getEventTimestamp();
        this.userId = event.getId();

        final User nullUser = new User(0, null, null, null);
        final User storeMe = event.applyTo(Optional.of(nullUser))
                                    .orElse(nullUser);

        this.givenName = storeMe.getGivenName();
        this.surname = storeMe.getSurname();
        this.email = storeMe.getEmail();
    }

    public <T extends AbstractUserEvent> T getEvent(final Class<T> type) {
        Preconditions.checkArgument(type.isAssignableFrom(eventType.getEventType()));
        return eventType.map(this);
    }

    protected User getUserChanges() {
        return new User(
                userId, givenName, surname, email);
    }
}
