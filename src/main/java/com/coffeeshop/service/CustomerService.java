package com.coffeeshop.service;

import static com.coffeeshop.model.StampCard.BONUS_THRESHOLD;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.filtering;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.coffeeshop.model.Customer;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.ProductType;
import com.coffeeshop.model.StampCard;

public class CustomerService {
    private static CustomerService instance;

    private final Map<String, Customer> customers = new HashMap<>();

    private CustomerService() {
    }

    public static synchronized CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    public void addCustomer(final Customer customer) {
        requireNonNull(customer, "Customer cannot be null");
        if (customers.containsKey(customer.getCode())) {
            throw new IllegalArgumentException(String.format("Customer already exist: %s", customer));
        }

        customers.put(customer.getCode(), customer);
    }

    public void updateCustomerStampCard(final Order order) {
        requireNonNull(order, "Order cannot be null");
        final var customerCode = order.customer().getCode();
        if (!customers.containsKey(customerCode))
            throw new IllegalArgumentException(String.format("Customer does not exist: %s", customerCode));

        final var customerStampCard = customers.get(customerCode).getStampCards().stream().filter(StampCard::isActive)
                .findFirst();
        final var newCountOfStamps = order.products().stream().collect(
                filtering(
                        product -> ProductType.BEVERAGE.equals(product.type()),
                        collectingAndThen(counting(), Long::intValue)
                )
        );
        final int[] newStampCards = { 0 };
        customerStampCard.ifPresentOrElse(
                stampCard -> {
                    final var currentCountOfStamps = stampCard.getStampCountPerOrder().getOrDefault(order.code(), 0);
                    final var totalCountOfStamps = currentCountOfStamps + newCountOfStamps;
                    final var updateCountOfStamps =
                            totalCountOfStamps <= BONUS_THRESHOLD ?
                                    totalCountOfStamps :
                                    BONUS_THRESHOLD - currentCountOfStamps;
                    customers.get(customerCode).updateStampCard(stampCard.getCode(), order.code(), updateCountOfStamps);
                    newStampCards[0] = totalCountOfStamps - updateCountOfStamps;
                },
                () -> {
                    newStampCards[0] = newCountOfStamps;
                }
        );
        final var size = (Math.floorDiv(newStampCards[0], 5) * BONUS_THRESHOLD) + BONUS_THRESHOLD;
        for (int index = BONUS_THRESHOLD; index <= size; index += BONUS_THRESHOLD) {
            final var stamps =
                    index < newStampCards[0] ? BONUS_THRESHOLD : newStampCards[0] - (index - BONUS_THRESHOLD);
            if (stamps > 0) {
                customers.get(customerCode).addStampCard(new StampCard(Map.of(order.code(), stamps)));
            }
        }
    }

    public Optional<Customer> getCustomerByCode(final String code) {
        requireNonNull(code, "Code cannot be null");

        return Optional.ofNullable(customers.get(code));
    }
}
