package com.coffeeshop.datasource;

import java.math.BigDecimal;

import com.coffeeshop.model.Product;
import com.coffeeshop.model.ProductType;

public enum ProductCatalog {
    COFFE_SMALL(new Product("COFFE_SMALL", "Coffe (Small)", BigDecimal.valueOf(2.50), ProductType.BEVERAGE)),
    COFFE_MEDIUM(new Product("COFFE_MEDIUM", "Coffe (Medium)", BigDecimal.valueOf(3.00), ProductType.BEVERAGE)),
    COFFE_LARGE(new Product("COFFE_LARGE", "Coffe (Large)", BigDecimal.valueOf(3.50), ProductType.BEVERAGE)),
    BACON_ROLL(new Product("BACON_ROLL", "Bacon Roll", BigDecimal.valueOf(4.50), ProductType.SNACK)),
    ORANGE_JUICE(new Product("ORANGE_JUICE", "Freshly Squeezed Orange Juice (0.25l)", BigDecimal.valueOf(3.95),
            ProductType.BEVERAGE)),
    EXTRA_MILK(new Product("EXTRA_MILK", "Extra Milk", BigDecimal.valueOf(0.30), ProductType.EXTRA)),
    FOAMED_MILK(new Product("FOAMED_MILK", "Foam Milk", BigDecimal.valueOf(0.50), ProductType.EXTRA)),
    ROAST_COFFE(new Product("ROAST_COFFE", "Special Roast Coffe", BigDecimal.valueOf(0.90), ProductType.EXTRA));

    private Product product;

    ProductCatalog(final Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

}
