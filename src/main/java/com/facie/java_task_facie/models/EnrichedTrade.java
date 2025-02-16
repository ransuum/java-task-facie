package com.facie.java_task_facie.models;

public record EnrichedTrade(String date, String productName, String currency, Double price) {
    public static EnrichedTrade toEnrichedTrade(Trade trade) {
        return new EnrichedTrade(
                trade.date(),
                trade.productName(),
                trade.currency(),
                trade.price()
        );
    }
}
