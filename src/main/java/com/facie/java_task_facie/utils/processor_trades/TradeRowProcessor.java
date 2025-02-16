package com.facie.java_task_facie.utils.processor_trades;

import com.facie.java_task_facie.models.Trade;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@FunctionalInterface
public interface TradeRowProcessor {
    /**
     * Process a single CSV row into a Trade.
     *
     * @param row           The CSV row.
     * @param discardedRows An AtomicInteger to count rows that are discarded.
     * @return Optional containing a Trade if successful; otherwise, empty.
     */
    Optional<Trade> processRow(String[] row, AtomicInteger discardedRows);
}
