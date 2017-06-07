package de.tobiasblaschke.eventsource.integration;

import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.EventFactory;
import de.tobiasblaschke.eventsource.sample.events.UserChangedName;
import de.tobiasblaschke.eventsource.sample.events.UserCreated;
import de.tobiasblaschke.eventsource.sample.events.UserDeleted;

import de.tobiasblaschke.eventsource.sample.persistence.sql.repositories.UserRepository;
import de.tobiasblaschke.eventsource.sample.service.UserService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.test.JpaTestBase;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class JpaPersistenceTest extends JpaTestBase {
    private final Instant onceUponATime = Instant.now().minus(10, ChronoUnit.DAYS);
    private final Instant aDayLater = onceUponATime.plus(1, ChronoUnit.DAYS);
    private final Instant twoDaysLater = onceUponATime.plus(2, ChronoUnit.DAYS);

    private final User john = new User(35, "John", "Doe", "jd@example.com");

    @Test
    public void shouldBeAbleToCrudAnUser() throws Exception {
        final EventFactory ef = getEventFactory();
        replay(
                ef.userCreated(john, onceUponATime),
                ef.userChangedName(john.getUserId(), "Doe", "Jane", aDayLater),
                ef.userDeleted(john.getUserId(), twoDaysLater)
        );

        printTable("user");

        { // All events should be in DB
            final UserRepository users = jpa.getUsers();
            final List<Event<Integer, User>> events = users.getAllEvents(onceUponATime);
            assertEquals(3, events.size());
        } // */

        { // Event history
            final UserService userService = getUserService();
            assertEquals(Optional.of(john), userService.get(john.getUserId(), onceUponATime));
            assertEquals("Jane", userService.get(john.getUserId(), aDayLater).get().getGivenName());
            assertEquals(Optional.empty(), userService.get(john.getUserId(), twoDaysLater));
            assertEquals(Optional.empty(), userService.get(john.getUserId())); // now
        }
    }
}
