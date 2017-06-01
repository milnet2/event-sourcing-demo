package de.tobiasblaschke.eventsource.scaffolding;

import de.tobiasblaschke.eventsource.scaffolding.events.Event;

public interface EventListener<E extends Event> {
    void onEvent(E event);
}
