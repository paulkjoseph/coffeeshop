package com.coffeeshop.service;

import static java.util.List.of;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.coffeeshop.model.Order;

public class OrderService {
    private static OrderService instance;

    private final Map<String, List<Order>> orders = new HashMap<>();

    private OrderService() {
    }

    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public void addOrder(final Order order) {
        requireNonNull(order, "Order cannot be null");

        final var customerOrders = orders.getOrDefault(order.customer().getCode(), of());
        List<Order> listOfOrders = new ArrayList<>();
        Stream.of(customerOrders, of(order)).forEach(listOfOrders::addAll);
        orders.put(order.customer().getCode(), listOfOrders);
    }

    public Optional<Order> getOrderByCode(final String code) {
        requireNonNull(code, "Code cannot be null");

        return orders.values().stream().flatMap(Collection::stream).filter(order -> order.code().equalsIgnoreCase(code))
                .findFirst();
    }
}
