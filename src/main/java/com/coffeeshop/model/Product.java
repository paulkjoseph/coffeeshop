package com.coffeeshop.model;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

public record Product(String ean, String name, BigDecimal price, ProductType type) {

    public Product {
        requireNonNull(name, "Name cannot be null");
        requireNonNull(name, "Price cannot be null");
        requireNonNull(name, "Type cannot be null");
    }

}
