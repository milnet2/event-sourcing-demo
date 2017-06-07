package de.tobiasblaschke.eventsource.scaffolding.impl;

import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.util.Optional;

public class PermissiveInconsistencyService implements InconsistencyService {
    @Override
    public <P> void report(Optional<P> dataBefore, Event<?, P> whenApplying, String message) {
        System.err.println(message + " when applying " + whenApplying + " (id " + whenApplying.getId() + ") to " + dataBefore);
    }
}
