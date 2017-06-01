package de.tobiasblaschke.eventsource.sample.domain;

public class Product {
    final int id;
    final String name;
    final int priceInCents;

    public Product(int id, String name, int priceInCents) {
        this.id = id;
        this.name = name;
        this.priceInCents = priceInCents;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriceInCents() {
        return priceInCents;
    }
}
