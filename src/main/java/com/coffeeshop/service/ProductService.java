package com.coffeeshop.service;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;

import com.coffeeshop.datasource.ProductCatalog;
import com.coffeeshop.model.Product;

public class ProductService {
    private static ProductService instance;

    private final List<Product> products = List.of(ProductCatalog.values()).stream().map(ProductCatalog::getProduct)
            .collect(toUnmodifiableList());

    private ProductService() {
    }

    public static synchronized ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    public List<Product> filterProducts(final String ean, final String name) {
        return products.stream()
                .filter(product -> product.ean().equalsIgnoreCase(ean) || product.name().equalsIgnoreCase(name))
                .collect(toUnmodifiableList());
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(final Product product) {
        requireNonNull(product, "Product cannot be null");
        if (filterProducts(product.ean(), product.name()).isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Product with the same ean or name already exist: %s", product));
        }
        products.add(product);
    }

}
