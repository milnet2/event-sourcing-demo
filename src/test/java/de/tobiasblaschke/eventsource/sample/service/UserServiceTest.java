package de.tobiasblaschke.eventsource.sample.service;

import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.*;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventSourceService;
import de.tobiasblaschke.eventsource.test.TestBase;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class UserServiceTest extends TestBase {
    private final Instant onceUponATime = Instant.now().minus(10, ChronoUnit.DAYS);
    private final Instant aDayLater = onceUponATime.plus(1, ChronoUnit.DAYS);
    private final Instant twoDaysLater = onceUponATime.plus(2, ChronoUnit.DAYS);

    private final User john = new User(1, "John", "Doe", "jd@example.com");
    private final Product coach = new Product(12, "Coach", 50);

    @Test
    public void shouldBePossibleToDeleteAUserBeforeItBoughtSomething() throws Exception {
        final State state = replay(
                new UserCreated(john, onceUponATime),
                new ProductAdded(coach, onceUponATime),
                new UserDeleted(john, aDayLater));
        final EventSourceService<Integer, User> users = new EventSourceService<>(state.getUsers());

        assertEquals(Optional.empty(), users.get(john.getUserId()));
    }

    @Test
    public void shouldNotBePossibleToDeleteAUserAfterItBoughtSomething() throws Exception {
        final State state = replay(
                new UserCreated(john, onceUponATime),
                new ProductAdded(coach, onceUponATime),
                new Bought(john, coach, aDayLater),
                new UserDeleted(john, twoDaysLater));
        final EventSourceService<Integer, User> users = new EventSourceService<>(state.getUsers());

        assertEquals(Optional.of(john), users.get(john.getUserId()));
    }

    @Test
    public void shouldBePossibleToChangeUsersName() throws Exception {
         final State state = replay(
                new UserCreated(john, onceUponATime),
                new UserChangedName(john.getUserId(), "Doe", "Jane", aDayLater));
        final EventSourceService<Integer, User> users = new EventSourceService<>(state.getUsers());

        assertEquals("Jane", users.get(john.getUserId()).get().getGivenName());
        assertEquals("jd@example.com", users.get(john.getUserId()).get().getEmail());
    }
}
