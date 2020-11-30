package com.coffeeshop.utils;

import static com.coffeeshop.datasource.ProductCatalog.BACON_ROLL;
import static com.coffeeshop.datasource.ProductCatalog.COFFE_LARGE;
import static com.coffeeshop.datasource.ProductCatalog.EXTRA_MILK;
import static com.coffeeshop.datasource.ProductCatalog.ORANGE_JUICE;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.coffeeshop.datasource.ProductCatalog;
import com.coffeeshop.model.Product;

public abstract class TestHelpers {

    protected List<Product> getProducts(final int size) {
        final var products = new ArrayList<Product>();
        products.addAll(nCopies(size, COFFE_LARGE.getProduct()));
        products.addAll(nCopies(size, ORANGE_JUICE.getProduct()));
        products.addAll(nCopies(size, BACON_ROLL.getProduct()));
        products.addAll(nCopies(size, EXTRA_MILK.getProduct()));
        return products;
    }

    protected List<Product> getProducts(final ProductCatalog... productCatalogs) {
        return List.of(productCatalogs).stream().map(ProductCatalog::getProduct)
                .collect(toUnmodifiableList());
    }

}
