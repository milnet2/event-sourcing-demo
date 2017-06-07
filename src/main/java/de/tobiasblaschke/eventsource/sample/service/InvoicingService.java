package de.tobiasblaschke.eventsource.sample.service;

import de.tobiasblaschke.eventsource.sample.domain.Invoice;
import de.tobiasblaschke.eventsource.sample.domain.OrderedProduct;
import de.tobiasblaschke.eventsource.sample.domain.Product;
import de.tobiasblaschke.eventsource.sample.domain.User;
import de.tobiasblaschke.eventsource.sample.events.UserChangedEmail;
import de.tobiasblaschke.eventsource.sample.persistence.InvoiceStore;
import de.tobiasblaschke.eventsource.sample.persistence.OrderStore;
import de.tobiasblaschke.eventsource.scaffolding.EventListener;
import de.tobiasblaschke.eventsource.scaffolding.InconsistencyService;
import de.tobiasblaschke.eventsource.scaffolding.impl.EventSourceService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InvoicingService extends EventSourceService<UUID, Invoice> implements EventListener<UserChangedEmail> {
    private final InvoiceStore invoiceStore;
    private final OrderStore orderStore;

    public InvoicingService(InvoiceStore invoiceStore, OrderStore orderStore, InconsistencyService inconsitencies) {
        super(invoiceStore, inconsitencies);
        this.invoiceStore = invoiceStore;
        this.orderStore = orderStore;
    }

    public Invoice createInvoice(final User buyer) {
        final List<Invoice> usersInvoices = invoiceStore.byUser(buyer, Instant.MIN);
        final List<OrderedProduct> usersOrders = orderStore.byUser(buyer, Instant.MIN);

        final List<Product> notInvoiced = usersOrders.stream()
                .filter(order -> ! usersInvoices.stream().anyMatch(
                        invoice -> invoice.getProducts().contains(order.getProduct())))
                .map(OrderedProduct::getProduct)
                .collect(Collectors.toList());

        return new Invoice(buyer, notInvoiced, 0);
    }

    @Override
    public void onEvent(UserChangedEmail event) {
        // TODO: Disallow E-Mail changes to sent invoices
    }

    public List<Invoice> byUser(User user) { // TODO: Would we really request this kind of info through the service? ... rather through the store
        return invoiceStore.byUser(user, Instant.MIN);
    }
}
