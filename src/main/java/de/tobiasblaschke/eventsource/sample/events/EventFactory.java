package de.tobiasblaschke.eventsource.sample.events;

import de.tobiasblaschke.eventsource.sample.domain.Invoice;
import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;

import java.time.Instant;

public class EventFactory {
    private final InconsistencyService inconsistencies;

    public EventFactory(InconsistencyService inconsistencies) {
        this.inconsistencies = inconsistencies;
    }

    public InvoiceSent invoiceSent(Invoice invoice, Instant eventTimestamp) {
        return new InvoiceSent(invoice.getId(), invoice, eventTimestamp, inconsistencies);
    }

    public PriceChanged priceChanged(int productId, int newPriceInCents, Instant eventTimestamp) {
        return new PriceChanged(productId, newPriceInCents, eventTimestamp, inconsistencies);
    }

    public ProductAdded productAdded(Product product, Instant eventTimestamp) {
        return new ProductAdded(product, eventTimestamp, inconsistencies);
    }

    public UserChangedEmail userChangedEmail(int userId, final String email, Instant eventTimestamp) {
        return new UserChangedEmail(userId, email, eventTimestamp, inconsistencies);
    }

    public UserChangedName userChangedName(int userId, final String surname, final String givenName, Instant eventTimestamp) {
        return new UserChangedName(userId, surname, givenName, eventTimestamp, inconsistencies);
    }

    public UserCreated userCreated(User user, Instant eventTimestamp) {
        return new UserCreated(user, eventTimestamp, inconsistencies);
    }

    public UserDeleted userDeleted(int userId, Instant eventTimestamp) {
        return new UserDeleted(userId, eventTimestamp, inconsistencies);
    }

    public Bought bought(final User user, final Product product, final Instant eventTimestamp) {
        return new Bought(user, product, eventTimestamp, inconsistencies);
    }
}
