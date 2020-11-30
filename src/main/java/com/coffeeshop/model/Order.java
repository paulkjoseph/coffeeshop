package com.coffeeshop.model;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(String code, Customer customer, List<Product>products, List<Product>rebate,
                    LocalDateTime createdAt) {

    public Order {
        requireNonNull(code, "Code cannot be null");
        requireNonNull(customer, "Customer cannot be null");
        requireNonNull(products, "Products cannot be null");
    }

    public Order(final Customer customer, final List<Product> products, final List<Product> rebate) {
        this(UUID.randomUUID().toString(), customer, products, rebate != null ? rebate : List.of(),
                LocalDateTime.now());
    }

}
