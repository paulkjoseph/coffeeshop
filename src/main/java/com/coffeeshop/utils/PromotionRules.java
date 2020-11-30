package com.coffeeshop.utils;

import static com.coffeeshop.model.ProductType.BEVERAGE;
import static com.coffeeshop.model.ProductType.EXTRA;
import static com.coffeeshop.model.ProductType.SNACK;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.filtering;
import static java.util.stream.Collectors.teeing;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.coffeeshop.model.Customer;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.Product;
import com.coffeeshop.model.StampCard;
import com.coffeeshop.service.CustomerService;

interface PromotionRulesInterface {
    List<Product> calculate(final Customer customer, final List<Product> products);

    default void apply(final Order order) {
    }
}

public enum PromotionRules implements PromotionRulesInterface {
    BEVERAGE_PROMOTION {
        @Override
        public List<Product> calculate(final Customer customer, final List<Product> products) {
            requireNonNull(customer, "Customer cannot be null");
            requireNonNull(products, "Products cannot be null");

            final var currentCountOfStamps = customer.getStampCards().stream().filter(StampCard::isActive)
                    .flatMap(stampCard -> stampCard.getStampCountPerOrder().values().stream())
                    .mapToInt(Integer::intValue)
                    .sum();
            final var beverages = products.stream().filter(product -> BEVERAGE.equals(product.type()))
                    .collect(toUnmodifiableList());
            final var newCountOfStamps = beverages.stream().collect(collectingAndThen(counting(), Long::intValue));
            final var countOfStamps = Math
                    .floor((currentCountOfStamps + newCountOfStamps) / Integer.valueOf(StampCard.BONUS_THRESHOLD)
                            .floatValue());

            if (countOfStamps < 1) {
                return List.of();
            }

            final var index = new AtomicInteger(1);
            record ProductIndex(int index, Product product) {
            }
            return beverages.stream()
                    .map(product -> new ProductIndex(index.getAndIncrement(), product))
                    .filter(productIndex -> productIndex.index() % StampCard.BONUS_THRESHOLD == 0)
                    .map(ProductIndex::product)
                    .collect(toUnmodifiableList());
        }

        @Override
        public void apply(final Order order) {
            CustomerService.getInstance().updateCustomerStampCard(order);
        }
    },
    EXTRA_PROMOTION {
        @Override
        public List<Product> calculate(final Customer customer, final List<Product> products) {
            requireNonNull(customer, "Customer cannot be null");
            requireNonNull(products, "Products cannot be null");

            final var isCustomerEligiblePromotion = products.stream().collect(teeing(
                    filtering(product -> BEVERAGE.equals(product.type()), counting()),
                    filtering(product -> SNACK.equals(product.type()), counting()),
                    (countBeverages, countSnacks) -> countBeverages > 0 && countSnacks > 0
            ));

            if (!isCustomerEligiblePromotion) {
                return List.of();
            }

            final var promotionProduct = products.stream().filter(product -> EXTRA.equals(product.type()))
                    .findFirst()
                    .orElse(null);

            return promotionProduct != null ? List.of(promotionProduct) : List.of();
        }

    }
}
