package de.tobiasblaschke.eventsource.scaffolding;

import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.util.Optional;

public interface InconsistencyService {
    <P> void report(Optional<P> dataBefore, Event<?, P> whenApplying, String message);
}
