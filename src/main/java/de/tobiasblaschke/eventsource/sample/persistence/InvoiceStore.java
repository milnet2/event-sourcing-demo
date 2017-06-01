package de.tobiasblaschke.eventsource.sample.persistence;

import de.tobiasblaschke.eventsource.sample.domain.Invoice;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.EventStore;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface InvoiceStore extends EventStore<UUID, Invoice> {
    List<Invoice> byUser(User user, Instant fromWhenOn);    // TODO: We should rather reference the users ID
    List<Invoice> byUser(int userId, Instant fromWhenOn);
}
