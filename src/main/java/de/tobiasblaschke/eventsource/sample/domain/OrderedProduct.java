package de.tobiasblaschke.eventsource.sample.domain;

public class OrderedProduct {
    final User user;
    final Product product;

    public OrderedProduct(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    public User getUser() {
        return user;
    }


    public Product getProduct() {
        return product;
    }
}
