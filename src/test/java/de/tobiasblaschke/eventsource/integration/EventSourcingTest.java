package de.tobiasblaschke.eventsource.integration;

import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.UserChangedName;
import de.tobiasblaschke.eventsource.sample.events.UserCreated;
import de.tobiasblaschke.eventsource.sample.events.UserDeleted;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventSourceService;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventStoreInMemory;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class EventSourcingTest {

    @Test
    public void shouldBeAbleToCrudAnUser() {
        final EventStore<Integer, User> userStore = new EventStoreInMemory<>(User.class);
        final EventSourceService<Integer, User> users = new EventSourceService<>(userStore);

        final Instant atSomePoint = Instant.now().minus(2, ChronoUnit.DAYS);
        final User john = new User(1, "John", "Doe", "jd@example.com");
        userStore.storeEvent(new UserCreated(john, atSomePoint));

        { // Create John...
            final List<Event<Integer, User>> events = userStore.getEventsFor(1, Instant.MIN);
            assertEquals(1, events.size());

            final Optional<User> retrieved = users.get(1);

            assertTrue(retrieved.isPresent());
            assertEquals(john, retrieved.get());
        }

        { // When the user was not present...
            assertFalse(users.get(1, atSomePoint.minus(1, ChronoUnit.SECONDS)).isPresent());
        }

        { // Change the Name
            userStore.storeEvent(new UserChangedName(john.getUserId(), "Doe", "Jane", Instant.now()));
            final Optional<User> jane = users.get(1);

            assertTrue(jane.isPresent());
            assertEquals("Jane", jane.get().getGivenName());

            assertEquals("John", users.get(1, atSomePoint.plus(1, ChronoUnit.MINUTES)).get().getGivenName());
        }

        { // Delete the user
            userStore.storeEvent(new UserDeleted(users.get(1).get(), Instant.now()));
            assertEquals(3, userStore.getEventsFor(1, Instant.MIN).size());
            assertFalse(users.get(1).isPresent());

            assertEquals("John", users.get(1, atSomePoint.plus(1, ChronoUnit.MINUTES)).get().getGivenName());
        }
    }

    @Ignore
    @Test
    public void shouldHandleSnapshots() {
        fail("Implement");
    }
}
