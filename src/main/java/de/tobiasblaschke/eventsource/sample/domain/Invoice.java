package de.tobiasblaschke.eventsource.sample.domain;

import java.util.List;
import java.util.UUID;

public class Invoice {
    final UUID id;
    final User user;
    final List<Product> products;
    final int payedInCents;
    private String email;

    public Invoice(User user, List<Product> products, int payedInCents) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.email = user.getEmail();
        this.products = products;
        this.payedInCents = payedInCents;
    }

    public int getTotalInCents() {
        return products.stream()
                .mapToInt(Product::getPriceInCents)
                .sum();
    }

    public User getUser() {
        return user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
