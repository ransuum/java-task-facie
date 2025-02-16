package com.facie.java_task_facie.service;

import com.facie.java_task_facie.enums.DateFormatter;
import com.facie.java_task_facie.models.EnrichedTrade;
import jakarta.annotation.PreDestroy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public interface TradeEnrichmentService {
    List<EnrichedTrade> enrichTradesFromCsv(InputStream csvInputStream);
    ByteArrayInputStream convertToCSV(List<EnrichedTrade> trades);
    String[] CSV_HEADER = {"date", "productName", "currency", "price"};

    static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateFormatter.DEFAULT.getDateTimeFormatter());
            return false;
        } catch (DateTimeParseException e) {
            return true;
        }
    }
    void shutdownExecutor();
}
