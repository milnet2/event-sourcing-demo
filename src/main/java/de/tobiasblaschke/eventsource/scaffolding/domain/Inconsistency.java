package de.tobiasblaschke.eventsource.scaffolding.domain;

import de.tobiasblaschke.eventsource.scaffolding.events.Event;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Inconsistency implements Serializable {
    private final UUID inconsistencyId;
    private final Optional<?> dataBefore;
    private final Event<?, ?> whenApplying;
    private final String message;
    private final boolean isHandled;

    public Inconsistency(Optional<?> dataBefore, Event<?, ?> whenApplying, String message, boolean isHandled) {
        this(UUID.randomUUID(), dataBefore, whenApplying, message, isHandled);
    }

    public Inconsistency(final UUID inconsistencyId, Optional<?> dataBefore, Event<?, ?> whenApplying, String message, boolean isHandled) {
        this.inconsistencyId = inconsistencyId;
        this.dataBefore = dataBefore;
        this.whenApplying = whenApplying;
        this.message = message;
        this.isHandled = isHandled;
    }

    public UUID getInconsistencyId() {
        return inconsistencyId;
    }

    public Optional<?> getDataBefore() {
        return dataBefore;
    }

    public Event<?, ?> getWhenApplying() {
        return whenApplying;
    }

    public String getMessage() {
        return message;
    }

    public boolean isHandled() {
        return isHandled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inconsistency that = (Inconsistency) o;
        return isHandled == that.isHandled &&
                Objects.equals(inconsistencyId, that.inconsistencyId) &&
                Objects.equals(dataBefore, that.dataBefore) &&
                Objects.equals(whenApplying, that.whenApplying) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inconsistencyId, dataBefore, whenApplying, message, isHandled);
    }
}
