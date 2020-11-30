package com.coffeeshop.service;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.filtering;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.coffeeshop.model.Customer;
import com.coffeeshop.model.ProductType;
import com.coffeeshop.model.StampCard;
import com.coffeeshop.utils.TestHelpers;
import org.junit.jupiter.api.Test;

class PromotionServiceTest extends TestHelpers {

    @Test
    void givenCustomerAndProductWhenCalculateBeveragePromotionThenOK() {
        final int size = 10;
        final var customer = new Customer("Paul Kibe");
        final var products = getProducts(size);
        final var newCountOfStamps = products.stream().collect(
                filtering(product -> ProductType.BEVERAGE.equals(product.type()),
                        collectingAndThen(counting(), Long::intValue)));

        CustomerService.getInstance().addCustomer(customer);
        final var result = PromotionService.getInstance().calculateBeveragePromotion(
                CustomerService.getInstance().getCustomerByCode(customer.getCode()).orElse(null), products);

        assertAll("BeveragePromotion",
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), Math.floorDiv(newCountOfStamps, StampCard.BONUS_THRESHOLD))
        );
    }

    @Test
    void givenCustomerAndProductWhenCalculateExtraPromotionThenOK() {
        final int size = 5;
        final var customer = new Customer("Paul Kibe");
        final var products = getProducts(size);

        CustomerService.getInstance().addCustomer(customer);
        final var result = PromotionService.getInstance().calculateExtraPromotion(
                CustomerService.getInstance().getCustomerByCode(customer.getCode()).orElse(null), products);

        assertAll("ExtraPromotion",
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), 1)
        );
    }
}