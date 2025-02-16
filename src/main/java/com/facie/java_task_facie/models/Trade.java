package com.facie.java_task_facie.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"date", "productName", "currency", "price"})
public record Trade(String date, Long productId,
                    String productName, String currency,
                    Double price) {
}
