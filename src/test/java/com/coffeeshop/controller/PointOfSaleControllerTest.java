package com.coffeeshop.controller;

import static com.coffeeshop.datasource.ProductCatalog.BACON_ROLL;
import static com.coffeeshop.datasource.ProductCatalog.COFFE_LARGE;
import static com.coffeeshop.datasource.ProductCatalog.EXTRA_MILK;
import static com.coffeeshop.datasource.ProductCatalog.ORANGE_JUICE;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.coffeeshop.datasource.ProductCatalog;
import com.coffeeshop.model.Customer;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.Product;
import com.coffeeshop.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PointOfSaleControllerTest {

    private static PointOfSaleController controller;

    @BeforeEach
    void setUp() {
        controller = new PointOfSaleController();
    }

    @Nested
    class CreateCustomer {
        @Test
        void givenNameWhenCreateCustomerThenReturnCustomer() {
            final var customerName = "Paul Kibe";

            final var customer = controller.createCustomer(customerName);

            assertAll("Customer",
                    () -> assertNotNull(customer),
                    () -> assertNotNull(customer.getCode()),
                    () -> assertEquals(customer.getName(), customerName)
            );
        }

        @Test
        void givenInvalidNameWhenCreateCustomerThenThrowException() {
            assertThrows(NullPointerException.class, () -> controller.createCustomer(null));
        }
    }

    @Nested
    class CreateOrder {
        @Test
        void givenCustomerAndProductsWhenCreateOrderThenReturnOrder() {
            final var customer = controller.createCustomer("Paul Kibe");

            final var products = new ArrayList<Product>();
            products.addAll(nCopies(10, COFFE_LARGE.getProduct()));
            products.addAll(nCopies(12, ORANGE_JUICE.getProduct()));
            products.addAll(nCopies(10, BACON_ROLL.getProduct()));
            products.addAll(nCopies(10, EXTRA_MILK.getProduct()));

            final var order = controller.createOrder(customer, products);
            final var stampCards = CustomerService.getInstance().getCustomerByCode(customer.getCode()).orElse(customer)
                    .getStampCards();

            assertAll("Order",
                    () -> assertNotNull(order),
                    () -> assertNotNull(order.customer()),
                    () -> assertNotNull(order.products()),
                    () -> assertNotNull(order.rebate()),
                    () -> assertEquals(order.products().size(), products.size()),
                    () -> assertEquals(order.rebate().size(), 5),
                    () -> assertEquals(stampCards.size(), 5)
            );
        }

        @Test
        void givenInvalidCustomerAndProductsWhenCreateOrderThenReturnException() {
            final var products = List.of(COFFE_LARGE).stream().map(ProductCatalog::getProduct)
                    .collect(toUnmodifiableList());

            assertThrows(NullPointerException.class, () -> controller.createOrder(null, products));
        }

    }

    @Nested
    class CreateReceipt {

        @Test
        void givenOrderWhenCreateReceiptThenReturnReceipt() {
            final var customer = new Customer("Paul Kibe");
            final var products = new ArrayList<Product>();
            products.addAll(nCopies(5, COFFE_LARGE.getProduct()));
            products.addAll(nCopies(5, BACON_ROLL.getProduct()));
            products.addAll(nCopies(5, EXTRA_MILK.getProduct()));
            final var subtotal = products.stream()
                    .map(Product::price)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();
            final var rebate = List.of(COFFE_LARGE, EXTRA_MILK).stream().map(ProductCatalog::getProduct)
                    .collect(toUnmodifiableList());
            final var discount = rebate.stream()
                    .map(Product::price)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();
            final var total = subtotal > discount ? subtotal - discount : subtotal;
            final var order = new Order(customer, products, rebate);

            final var receipt = controller.createReceipt(order);

            assertAll("Receipt",
                    () -> assertNotNull(receipt),
                    () -> assertEquals(receipt.subtotal(), BigDecimal.valueOf(subtotal)),
                    () -> assertEquals(receipt.discount(), BigDecimal.valueOf(discount)),
                    () -> assertEquals(receipt.total(), BigDecimal.valueOf(total)),
                    () -> assertEquals(receipt.products().size(), products.size()),
                    () -> assertEquals(receipt.reabte().size(), rebate.size()),
                    () -> assertTrue(receipt.details().contains(customer.getName()))
            );
        }

        @Test
        void givenInvalidOrderWhenCreateReceiptThenReturnException() {
            assertThrows(NullPointerException.class, () -> controller.createReceipt(null));
        }
    }
}