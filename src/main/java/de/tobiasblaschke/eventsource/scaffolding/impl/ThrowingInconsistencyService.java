package de.tobiasblaschke.eventsource.scaffolding.impl;

import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.util.Optional;

public class ThrowingInconsistencyService implements InconsistencyService {
    public static class InconsistentEventsException extends RuntimeException {
        private final Optional<?> dataBefore;
        private final Event<?, ?> whenApplying;

        public <P> InconsistentEventsException(String message, Optional<P> dataBefore, Event<?, P> whenApplying) {
            super(message + " when applying " + whenApplying + " (id " + whenApplying.getId() + ") to " + dataBefore);
            this.dataBefore = dataBefore;
            this.whenApplying = whenApplying;
        }
    }

    @Override
    public <P> void report(Optional<P> dataBefore, Event<?, P> whenApplying, String message) {
        throw new InconsistentEventsException(message, dataBefore, whenApplying);
    }
}
