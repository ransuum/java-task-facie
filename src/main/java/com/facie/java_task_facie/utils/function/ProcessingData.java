package com.facie.java_task_facie.utils.function;

import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessingData {
    private final Logger logger;

    public ProcessingData(Logger logger) {
        this.logger = logger;
    }

    public Long parseLong(String value, String errorMessage, List<String> errors) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            errors.add(errorMessage + ": " + value);
            return null;
        }
    }

    public Double parseDouble(String value, String errorMessage, List<String> errors) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            errors.add(errorMessage + ": " + value);
            return null;
        }
    }

    public void logAndDiscard(AtomicInteger discardedRows, String message, Object... args) {
        logger.error(message, args);
        discardedRows.incrementAndGet();
    }
}
