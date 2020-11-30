package com.coffeeshop.model;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.List;

public record Receipt(BigDecimal subtotal, BigDecimal discount, BigDecimal total, List<Product>products,
                      List<Product>reabte, String details) {

    public Receipt {
        requireNonNull(subtotal, "Subtotal cannot be null");
        requireNonNull(total, "Total cannot be null");
        requireNonNull(products, "Products cannot be null");
        requireNonNull(details, "Details cannot be null");
    }

}
