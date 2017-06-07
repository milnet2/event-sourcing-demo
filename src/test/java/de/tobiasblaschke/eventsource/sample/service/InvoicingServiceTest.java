package de.tobiasblaschke.eventsource.sample.service;

import de.tobiasblaschke.eventsource.sample.domain.Invoice;
import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.*;
import de.tobiasblaschke.eventsource.test.TestBase;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class InvoicingServiceTest extends TestBase {
    private final Instant onceUponATime = Instant.now().minus(10, ChronoUnit.DAYS);
    private final Instant aDayLater = onceUponATime.plus(1, ChronoUnit.DAYS);
    private final Instant twoDaysLater = onceUponATime.plus(2, ChronoUnit.DAYS);

    private final User john = new User(1, "John", "Doe", "jd@example.com");
    private final Product coach = new Product(12, "Coach", 50);

    @Test
    public void invoicesShouldBeUnaffectedByPriceChanges() throws Exception {
        final State state = replay(
                ef -> ef.userCreated(john, onceUponATime),
                ef -> ef.productAdded(coach, onceUponATime),
                ef -> ef.bought(john, coach, aDayLater),
                ef -> ef.priceChanged(coach.getId(), 52, twoDaysLater));

        final InvoicingService invoicing = new InvoicingService(state.getInvoices(), state.getOrders(), state.getInconsistencies());
        final Invoice invoice = invoicing.createInvoice(john);

        assertEquals(50, invoice.getTotalInCents());
    }

    @Test
    public void invoicesShouldBeSentToTheLatestEmail() throws Exception {
        final String changedEmail = "foo@example.com";
        final State state = new State();
        final InvoicingService invoicing = new InvoicingService(state.getInvoices(), state.getOrders(), state.getInconsistencies());

        replay(state,
                ef -> ef.userCreated(john, onceUponATime),
                ef -> ef.productAdded(coach, onceUponATime),
                ef -> ef.bought(john, coach, aDayLater)
        );

        final Invoice invoice = invoicing.createInvoice(john);
        assertEquals(john.getEmail(), invoice.getEmail());

        replay(state,
                ef -> ef.userChangedEmail(john.getUserId(), changedEmail, twoDaysLater),
                ef -> ef.invoiceSent(invoice, Instant.now()));

        final List<Invoice> sentInvoices = state.getInvoices().byUser(john, Instant.MIN);
        assertEquals(1, sentInvoices.size());
        assertEquals(changedEmail, sentInvoices.get(0).getEmail());
    }

    @Test
    public void shouldNotReactToEmailUpdatesAfterInvoiceWasSent() throws Exception {
        final State state = new State();
        final InvoicingService invoicing = new InvoicingService(state.getInvoices(), state.getOrders(), state.getInconsistencies());

        replay(state,
                ef -> ef.userCreated(john, onceUponATime),
                ef -> ef.productAdded(coach, onceUponATime),
                ef -> ef.bought(john, coach, aDayLater),
                ef -> ef.invoiceSent(invoicing.createInvoice(john), twoDaysLater),
                ef -> ef.userChangedEmail(john.getUserId(), "unreflected@example.com", twoDaysLater)
        );

        final List<Invoice> sentInvoices = state.getInvoices().byUser(john, Instant.MIN);
        assertEquals(1, sentInvoices.size());
        assertEquals(john.getEmail(), sentInvoices.get(0).getEmail());
    }

}