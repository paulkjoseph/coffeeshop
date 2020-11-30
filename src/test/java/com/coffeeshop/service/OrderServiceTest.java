package com.coffeeshop.service;

import static com.coffeeshop.datasource.ProductCatalog.COFFE_LARGE;
import static com.coffeeshop.datasource.ProductCatalog.EXTRA_MILK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.coffeeshop.model.Customer;
import com.coffeeshop.model.Order;
import com.coffeeshop.utils.TestHelpers;
import org.junit.jupiter.api.Test;

class OrderServiceTest extends TestHelpers {

    @Test
    void givenOrderWhenAddOrderThenOK() {
        final int size = 10;
        final var customer = new Customer("Paul Kibe");
        final var products = getProducts(size);
        final var rebate = getProducts(COFFE_LARGE, EXTRA_MILK);
        final var order = new Order(customer, products, rebate);

        CustomerService.getInstance().addCustomer(customer);
        OrderService.getInstance().addOrder(order);
        final var result = OrderService.getInstance().getOrderByCode(order.code()).orElse(null);

        assertAll("Order",
                () -> assertNotNull(result),
                () -> assertEquals(result.code(), order.code()),
                () -> assertEquals(result.customer().getName(), order.customer().getName()),
                () -> assertEquals(result.products().size(), order.products().size()),
                () -> assertEquals(result.rebate().size(), order.rebate().size())
        );
    }

}