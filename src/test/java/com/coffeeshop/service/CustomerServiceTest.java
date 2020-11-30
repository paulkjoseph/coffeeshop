package com.coffeeshop.service;

import static com.coffeeshop.datasource.ProductCatalog.COFFE_LARGE;
import static com.coffeeshop.datasource.ProductCatalog.EXTRA_MILK;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.filtering;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.coffeeshop.model.Customer;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.ProductType;
import com.coffeeshop.model.StampCard;
import com.coffeeshop.utils.TestHelpers;
import org.junit.jupiter.api.Test;

class CustomerServiceTest extends TestHelpers {

    @Test
    void givenCustomerWhenAddCustomerThenOK() {
        final var customer = new Customer("Paul Kibe");

        CustomerService.getInstance().addCustomer(customer);
        final var result = CustomerService.getInstance().getCustomerByCode(customer.getCode()).orElse(null);

        assertAll("Customer",
                () -> assertNotNull(result),
                () -> assertEquals(result.getCode(), customer.getCode()),
                () -> assertEquals(result.getName(), customer.getName())
        );
    }

    @Test
    void givenOrderWhenUpdateCustomerStampCardThenOK() {
        final int size = 10;
        final var customer = new Customer("Paul Kibe");
        final var products = getProducts(size);
        final var rebate = getProducts(COFFE_LARGE, EXTRA_MILK);
        final var newCountOfStamps = products.stream().collect(
                filtering(product -> ProductType.BEVERAGE.equals(product.type()),
                        collectingAndThen(counting(), Long::intValue)));
        final var order = new Order(customer, products, rebate);

        CustomerService.getInstance().addCustomer(customer);
        CustomerService.getInstance().updateCustomerStampCard(order);
        final var result = CustomerService.getInstance().getCustomerByCode(customer.getCode()).orElse(null);

        assertAll("CustomerStampCard",
                () -> assertNotNull(result),
                () -> assertEquals(result.getCode(), customer.getCode()),
                () -> assertEquals(result.getName(), customer.getName()),
                () -> assertEquals(result.getStampCards().size(),
                        Math.floorDiv(newCountOfStamps, StampCard.BONUS_THRESHOLD))
        );
    }

    @Test
    void givenCustomerCodeWhenGetCustomerByCodeThenOK() {
        final var customer = new Customer("Paul Kibe");

        CustomerService.getInstance().addCustomer(customer);
        final var result = CustomerService.getInstance().getCustomerByCode(customer.getCode()).orElse(null);

        assertAll("Customer",
                () -> assertNotNull(result),
                () -> assertEquals(result.getCode(), customer.getCode()),
                () -> assertEquals(result.getName(), customer.getName())
        );
    }
}