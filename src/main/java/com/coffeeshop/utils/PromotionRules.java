package com.coffeeshop.utils;

import java.util.List;

import com.coffeeshop.model.Customer;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.Product;
import com.coffeeshop.service.CustomerService;
import com.coffeeshop.service.PromotionService;

interface PromotionRulesInterface {
    List<Product> calculate(final Customer customer, final List<Product> products);

    default void apply(final Order order) {
    }
}

public enum PromotionRules implements PromotionRulesInterface {
    BEVERAGE_PROMOTION {
        @Override
        public List<Product> calculate(final Customer customer, final List<Product> products) {
            return PromotionService.getInstance().calculateBeveragePromotion(customer, products);
        }

        @Override
        public void apply(final Order order) {
            CustomerService.getInstance().updateCustomerStampCard(order);
        }
    },
    EXTRA_PROMOTION {
        @Override
        public List<Product> calculate(final Customer customer, final List<Product> products) {
            return PromotionService.getInstance().calculateExtraPromotion(customer, products);
        }

    }
}
