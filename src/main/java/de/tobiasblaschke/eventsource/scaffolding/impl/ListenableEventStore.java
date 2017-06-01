package de.tobiasblaschke.eventsource.scaffolding.impl;

import com.google.common.collect.ImmutableList;
import de.tobiasblaschke.eventsource.scaffolding.EventListener;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;
import de.tobiasblaschke.eventsource.scaffolding.events.Event;
import de.tobiasblaschke.eventsource.scaffolding.events.Snapshot;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  Allows listening for events on storing them.
 *  Dispatch them to the "real" store by listening for them
 */
public class ListenableEventStore implements EventStore {
    private final List<EventWiring<?, ?>> wiring;
    private final Optional<EventStore<Object, Object>> deadLetter;

    public static class WiringBuilder {
        private final ImmutableList.Builder<EventWiring<?, ?>> wiring = ImmutableList.builder();
        private @Nullable EventStore<Object, Object> deadLetter = null;

        public <I, P> WiringBuilder wire(final Class<? extends Event<I, P>> eventType, final EventStore<I, P> handler) {
            wiring.add(new EventWiring<>(eventType, handler));
            return this;
        }

        public <I, P, E extends Event<I, P>> WiringBuilder wire(final Class<E> eventType, final EventListener<E> handler) {
            wiring.add(new ServiceWiring<>(eventType, handler));
            return this;
        }

        public WiringBuilder deadLetter(final EventStore<Object, Object> deadLetter) {
            this.deadLetter = deadLetter;
            return this;
        }

        public ListenableEventStore build() {
            return new ListenableEventStore(wiring.build(), Optional.ofNullable(deadLetter));
        }
    }

    public ListenableEventStore(List<EventWiring<?, ?>> wiring, Optional<EventStore<Object, Object>> deadLetter) {
        this.wiring = wiring;
        this.deadLetter = deadLetter;
    }

    public static WiringBuilder builder() {
        return new WiringBuilder();
    }

    @Override
    public void storeEvent(final Event event) {
        final List<EventStore> handlers = handlersFor(event);

        if (handlers.isEmpty()) {
            deadLetter.ifPresent(dl -> dl.storeEvent(event));
        } else {
            handlers
                .forEach(handler -> handler.storeEvent(event));
        }
    }

    private List<EventStore> handlersFor(final Event event) {
         return wiring.stream()
                .filter(handler -> handler.isHandling(event))
                .map(EventWiring::getHandler)
                .collect(Collectors.toList());
    }

    @Override
    public void storeSnapshot(final Snapshot snapshot) {
        final List<EventStore> handlers = handlersFor(snapshot);

        if (handlers.isEmpty()) {
            deadLetter.ifPresent(dl -> dl.storeSnapshot(snapshot));
        } else {
            handlers
                    .forEach(handler -> handler.storeSnapshot(snapshot));
        }
    }

    private List<EventStore> handlersFor(final Snapshot snapshot) {
         return wiring.stream()
                .filter(handler -> handler.isHandling(snapshot))
                .map(EventWiring::getHandler)
                .collect(Collectors.toList());
    }

    @Override
    @Deprecated // You _will_ get ClassCast-exceptions using this method. Please provide eventType.
    public List<Event> getEventsFor(Object id, Instant fromWhenOn) {
        return getEventsFor(id, fromWhenOn, Event.class);
    }

    public <I, T extends Event<I, ?>> List<T> getEventsFor(final I id, final Instant fromWhenOn, final Class<T> eventType) {
        return handlersForEvent(id, eventType)
                .map(handler -> handler.getEventsFor(id, fromWhenOn))
                .flatMap(handlersEvents -> (Stream<T>) handlersEvents.stream()) // safe cast
                .collect(Collectors.toList());
    }

    private Stream<EventStore> handlersForEvent(Object id, Class<? extends Event> eventType) {
          return wiring.stream()
                .filter(handler -> handler.isTakingId(id.getClass()))
                .filter(handler -> handler.isReturningEvent(eventType))
                .map(EventWiring::getHandler);
    }

    @Override
    @Deprecated // You _will_ get ClassCast-exceptions using this method. Please provide eventType.
    public Optional<Snapshot> getSnapshotFor(Object id, Instant atTime) {
        return null;
    }

    @Deprecated // Dangerous! There may be multiple snapshots from different handlers if the id is not unique across them
    public <I, P> Optional<Snapshot<I, P>> getSnapshotFor(final I id, final Instant atTime, final Class<P> payloadType) {
        return handlersForSnapshot(id, payloadType)
                .map(handler -> handler.getSnapshotFor(id, atTime))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(snapshot -> (Snapshot<I, P>) snapshot)
                .sorted(Comparator.comparing(Snapshot::getSnapshotTimestamp))
                .findFirst();
    }

    private Stream<EventStore> handlersForSnapshot(Object id, Class<?> payloadType) {
          return wiring.stream()
                .filter(handler -> handler.isTakingId(id.getClass()))
                .filter(handler -> handler.isReturningPayload(payloadType))
                .map(EventWiring::getHandler);
    }

    @Override
    public List<Event> getAllEvents(final Instant fromWhenOn) {
        return wiring.stream()
                .map(EventWiring::getHandler)
                .flatMap(store -> store.getAllEvents(fromWhenOn).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Class getPayloadType() {
        // We could use a cone-type, but what would that help
        return Object.class;
    }

}



class EventWiring<I, P> {
    final Class<? extends Event<I, P>> eventType;
    final Class<I> idType;
    final Class<P> payloadType;
    final EventStore<I, P> handler;

    public EventWiring(Class<? extends Event<I, P>> eventType, EventStore<I, P> handler) {
        this.eventType = eventType;
        this.handler = handler;
        this.idType = findTypeOfId(handler);
        this.payloadType = handler.getPayloadType();
    }

    private static <I> Class<I> findTypeOfId(final EventStore<I, ?> handler) {
        return Arrays.stream(handler.getClass()
                .getDeclaredMethods())
                .filter(method -> "getSnapshotFor".equals(method.getName()))
                .map(method -> (Class<I>) method.getParameterTypes()[0])
                .findAny()
                .orElseThrow(() -> new UnsupportedOperationException("EventHandler should contain a getSnapshotFor-method"));
    }

    public boolean isHandling(final Event event) {
        return eventType.isAssignableFrom(event.getClass()) &&
                idType.isAssignableFrom(event.getId().getClass());
    }

    public boolean isHandling(final Snapshot snapshot) {
        return idType.isAssignableFrom(snapshot.getId().getClass()) &&
                payloadType.isAssignableFrom(snapshot.getData().getClass());
    }

    public boolean isTakingId(final Class<?> idType) {
        return this.idType.isAssignableFrom(idType);
    }

    public boolean isReturningPayload(final Class<?> payloadType) {
        return payloadType.isAssignableFrom(this.payloadType);
    }

    public boolean isReturningEvent(final Class<?> eventType) {
        return eventType.isAssignableFrom(this.eventType);
    }

    public EventStore<I, P> getHandler() {
        return handler;
    }
}

class ServiceWiring<I, P, E extends Event<I, P>> extends EventWiring<I, P> {
    final Class<E> eventType;

    public ServiceWiring(Class<E> eventType, EventListener<E> handler) {
        super(eventType, new ServiceAdapter<>(handler));
        this.eventType = eventType;
    }

    @Override
    public boolean isHandling(Event event) {
        return eventType.isAssignableFrom(event.getClass());
    }

    @Override
    public boolean isHandling(Snapshot snapshot) {
        return false;
    }

    @Override
    public boolean isTakingId(Class<?> idType) {
        return false;
    }

    @Override
    public boolean isReturningPayload(Class<?> payloadType) {
        return false;
    }

    @Override
    public boolean isReturningEvent(Class<?> eventType) {
        return false;
    }
}

class ServiceAdapter<I, P, E extends Event<I, P>> implements EventStore<I, P> {
    final EventListener<E> delegate;

    public ServiceAdapter(EventListener<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Event<I, P>> getEventsFor(I id, Instant fromWhenOn) {
        return ImmutableList.of();
    }

    @Override
    public Optional<Snapshot<I, P>> getSnapshotFor(I id, Instant atTime) {
        return Optional.empty();
    }

    @Override
    public List<Event<I, P>> getAllEvents(Instant fromWhenOn) {
        return ImmutableList.of();
    }

    @Override
    public void storeEvent(Event<I, P> event) {
        delegate.onEvent((E) event);
    }

    @Override
    public void storeSnapshot(Snapshot<I, P> snapshot) {

    }

    @Override
    public Class<P> getPayloadType() {
        return null;
    }
}