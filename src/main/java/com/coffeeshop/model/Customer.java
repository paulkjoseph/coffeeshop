package com.coffeeshop.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

interface CustomerInterface {
    void addStampCard(final StampCard stampCard);

    void updateStampCard(final String stampCardCode, final String orderCode, final int countOfStamps);
}

public final class Customer implements CustomerInterface {
    private String name;
    private String code;
    private List<StampCard> stampCards;

    private Customer() {
    }

    public Customer(final String name) {
        requireNonNull(name, "Name cannot be null");

        this.name = name;
        this.code = UUID.randomUUID().toString();
        this.stampCards = new ArrayList<>(List.of(new StampCard()));
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public List<StampCard> getStampCards() {
        return stampCards;
    }

    @Override
    public void addStampCard(final StampCard stampCard) {
        if (stampCards.stream().anyMatch(StampCard::isActive)) {
            throw new IllegalArgumentException(String.format("Active stamp card already exist: %s", stampCard));
        }

        this.stampCards.add(stampCard);
    }

    @Override
    public void updateStampCard(final String stampCardCode, final String orderCode, final int countOfStamps) {
        requireNonNull(stampCardCode, "Stamp card code cannot be null");
        requireNonNull(orderCode, "Order code cannot be null");

        stampCards.forEach(stampCard -> {
            if (stampCard.getCode().equals(stampCardCode)) {
                stampCard.updateStampCard(orderCode, countOfStamps);
            }
        });
    }
}
