package com.coffeeshop.controller;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import com.coffeeshop.datasource.ReceiptTemplate;
import com.coffeeshop.model.Customer;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.Product;
import com.coffeeshop.model.Receipt;
import com.coffeeshop.service.CustomerService;
import com.coffeeshop.service.OrderService;
import com.coffeeshop.utils.PromotionRules;

interface PointOfSaleControllerInterface {
    Customer createCustomer(final String name);

    Order createOrder(final Customer customer, final List<Product> products);

    Receipt createReceipt(final Order order);
}

public record PointOfSaleController() implements PointOfSaleControllerInterface {

    @Override
    public Customer createCustomer(final String name) {
        requireNonNull(name, "Name code cannot be null");

        final var customer = new Customer(name);
        CustomerService.getInstance().addCustomer(customer);

        return customer;
    }

    @Override
    public Order createOrder(final Customer customer, final List<Product> products) {
        requireNonNull(customer, "Customer code cannot be null");
        requireNonNull(products, "Products code cannot be null");

        final var promotionRules = List.of(PromotionRules.values());
        final var rebate = promotionRules.stream()
                .map(rules -> rules.calculate(customer, products))
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());

        final var order = new Order(customer, products, rebate);

        promotionRules.forEach(rule -> rule.apply(order));

        OrderService.getInstance().addOrder(order);

        return order;
    }

    @Override
    public Receipt createReceipt(final Order order) {
        requireNonNull(order, "Order code cannot be null");

        final var subtotal = order.products().stream()
                .map(Product::price)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        final var discount = order.rebate().stream()
                .map(Product::price)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        final var total = subtotal > discount ? subtotal - discount : subtotal;

        final var allProducts = order.products().stream()
                .collect(groupingBy(Product::ean, toUnmodifiableList()))
                .entrySet()
                .stream()
                .map(stringListEntry -> {
                    final var product = stringListEntry.getValue().get(0);
                    final var quantity = stringListEntry.getValue().size();
                    return "%s %s X %s = %s".formatted(product.name(), quantity, product.price().setScale(2, HALF_UP),
                            product.price().multiply(BigDecimal.valueOf(quantity)).setScale(2, HALF_UP));
                })
                .collect(joining("\n"));
        final var promotionProducts = order.rebate().stream()
                .collect(groupingBy(Product::ean, toUnmodifiableList()))
                .entrySet()
                .stream()
                .map(stringListEntry -> {
                    final var product = stringListEntry.getValue().get(0);
                    final var quantity = stringListEntry.getValue().size();
                    return "%s %s X %s = - %s".formatted(product.name(), quantity, product.price().setScale(2, HALF_UP),
                            product.price().multiply(BigDecimal.valueOf(quantity)).setScale(2, HALF_UP));
                })
                .collect(joining("\n"));

        final var header = ReceiptTemplate.HEADER.getContent();
        final var body = ReceiptTemplate.BODY.getContent()
                .formatted(order.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), allProducts,
                        promotionProducts, subtotal, discount, total);
        final var footer = ReceiptTemplate.FOOTER.getContent()
                .formatted(order.customer().getName());

        final var receiptDetails = """
                %s
                %s
                %s
                """.formatted(header, body, footer);

        return new Receipt(BigDecimal.valueOf(subtotal), BigDecimal.valueOf(discount), BigDecimal.valueOf(total),
                order.products(), order.rebate(), receiptDetails);
    }
}
