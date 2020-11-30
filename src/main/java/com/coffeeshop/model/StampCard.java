package com.coffeeshop.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

interface StampCardInterface {
    void updateStampCard(final String orderCode, final int countOfStamps);
}

public final class StampCard implements StampCardInterface {
    public static final int BONUS_THRESHOLD = 5;

    private String code;
    private Map<String, Integer> stampCountPerOrder;

    public StampCard() {
        this.code = UUID.randomUUID().toString();
        this.stampCountPerOrder = new HashMap<>();
    }

    public StampCard(final Map<String, Integer> stampCountPerOrder) {
        this.code = UUID.randomUUID().toString();
        this.stampCountPerOrder = stampCountPerOrder;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Integer> getStampCountPerOrder() {
        return stampCountPerOrder;
    }

    public int getStamps() {
        return stampCountPerOrder.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isActive() {
        return getStamps() < BONUS_THRESHOLD;
    }

    @Override
    public void updateStampCard(final String orderCode, final int countOfStamps) {
        if (!isActive()) {
            throw new IllegalArgumentException(String.format("Stamp card is inactive: %s", code));
        }
        final var currentCountOfStamps = stampCountPerOrder.getOrDefault(orderCode, 0);
        stampCountPerOrder.put(orderCode, currentCountOfStamps + countOfStamps);
    }
}
