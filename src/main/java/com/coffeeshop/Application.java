package com.coffeeshop;

import static com.coffeeshop.datasource.ProductCatalog.BACON_ROLL;
import static com.coffeeshop.datasource.ProductCatalog.COFFE_LARGE;
import static com.coffeeshop.datasource.ProductCatalog.COFFE_SMALL;
import static com.coffeeshop.datasource.ProductCatalog.EXTRA_MILK;
import static com.coffeeshop.datasource.ProductCatalog.ORANGE_JUICE;
import static com.coffeeshop.datasource.ProductCatalog.ROAST_COFFE;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.logging.Logger;

import com.coffeeshop.controller.PointOfSaleController;
import com.coffeeshop.datasource.ProductCatalog;

public class Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {
        final var pointOfSaleController = new PointOfSaleController();

        final var customer = pointOfSaleController.createCustomer("Paul Kibe");

        final var products = List.of(COFFE_LARGE, EXTRA_MILK, COFFE_SMALL, ROAST_COFFE, BACON_ROLL, ORANGE_JUICE)
                .stream().map(ProductCatalog::getProduct).collect(toUnmodifiableList());
        final var order = pointOfSaleController.createOrder(customer, products);

        final var receipt = pointOfSaleController.createReceipt(order);
        LOGGER.info(receipt);
    }

}
