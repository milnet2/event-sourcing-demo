package de.tobiasblaschke.eventsource.scaffolding.impl;

import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.domain.Inconsistency;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.events.InconsistencyAccepted;
import de.tobiasblaschke.eventsource.scaffolding.events.InconsistencyDetected;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class StoringInconsistencyService extends EventSourceService<UUID, Inconsistency> implements InconsistencyService {
    public StoringInconsistencyService(EventStore<UUID, Inconsistency> store) {
        super(store, new ThrowingInconsistencyService());
    }

    @Override
    public <P> void report(Optional<P> dataBefore, Event<?, P> whenApplying, String message) {
        final InconsistencyDetected event = new InconsistencyDetected(
                new Inconsistency(dataBefore, whenApplying, message, false),
                Instant.now());
        getStore().storeEvent(event);
    }

    public List<Inconsistency> getUnhandled(final Instant since) {
        // TODO: Use snapshots
        final List<Event<UUID, Inconsistency>> events = getStore().getAllEvents(since);

        return events.stream()
                .collect(Collectors.groupingBy(Event::getId))
                .values().stream()
                .map(eventList -> replay(Optional.empty(), eventList, Instant.MAX))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(inconsistency -> ! inconsistency.isHandled())
                .collect(Collectors.toList());
    }

    public void retryNow(final Inconsistency inconsistency, final EventStore handler) {
        throw new UnsupportedOperationException();
    }

    public void revert(final Inconsistency inconsistency, final EventStore handler) {
        throw new UnsupportedOperationException();
    }

    public void acknowledge(final Inconsistency inconsistency) {
        final InconsistencyAccepted event = new InconsistencyAccepted(inconsistency.getInconsistencyId(), Instant.now());
        getStore().storeEvent(event);
    }
}
