package com.facie.java_task_facie.enums;


public enum ErrorType {
    MISSING_PRODUCT_NAME("Missing Product Name");

    public final String val;

    ErrorType(String val) {
        this.val = val;
    }
}
