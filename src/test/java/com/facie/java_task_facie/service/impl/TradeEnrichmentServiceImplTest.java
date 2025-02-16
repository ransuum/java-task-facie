package com.facie.java_task_facie.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.facie.java_task_facie.models.EnrichedTrade;
import com.facie.java_task_facie.models.Trade;
import com.facie.java_task_facie.utils.processor_trades.TradeRowProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeEnrichmentServiceImplTest {
    @Mock
    private TradeRowProcessor tradeRowProcessor;

    @InjectMocks
    private TradeEnrichmentServiceImpl tradeEnrichmentService;

    @Test
    void testEnrichTradesFromCsv_validRows() throws Exception {
        String csvContent = """
                date,productId,currency,price
                2020-01-01,1,USD,100.50
                2020-01-02,2,EUR,200.75
                """;
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        when(tradeRowProcessor.processRow(any(String[].class), any(AtomicInteger.class)))
                .thenAnswer(invocation -> {
                    String[] row = invocation.getArgument(0);
                    if (row.length < 4) {
                        return Optional.empty();
                    }
                    try {
                        Long productId = Long.parseLong(row[1]);
                        double price = Double.parseDouble(row[3]);
                        String date = row[0];
                        String productName = "Product " + row[1];
                        Trade trade = new Trade(date, productId, productName, row[2], price);
                        return Optional.of(trade);
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                });

        List<EnrichedTrade> result = tradeEnrichmentService.enrichTradesFromCsv(inputStream);
        assertNotNull(result);
        assertEquals(2, result.size());

        EnrichedTrade expected1 = EnrichedTrade.toEnrichedTrade(new Trade("2020-01-01", 1L, "Product 1", "USD", 100.50));
        EnrichedTrade expected2 = EnrichedTrade.toEnrichedTrade(new Trade("2020-01-02", 2L, "Product 2", "EUR", 200.75));

        assertTrue(result.contains(expected1));
        assertTrue(result.contains(expected2));
    }

    @Test
    void testEnrichTradesFromCsv_withInvalidRow() throws Exception {
        String csvContent = """
                date,productId,currency,price
                2020-01-01,1,USD,100.50
                2020-01-02,bad,EUR,200.75
                """;
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        when(tradeRowProcessor.processRow(any(String[].class), any(AtomicInteger.class)))
                .thenAnswer(invocation -> {
                    String[] row = invocation.getArgument(0);
                    if ("bad".equals(row[1])) return Optional.empty();
                    try {
                        Long productId = Long.parseLong(row[1]);
                        double price = Double.parseDouble(row[3]);
                        String date = row[0];
                        String productName = "Product " + row[1];
                        Trade trade = new Trade(date, productId, productName, row[2], price);
                        return Optional.of(trade);
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                });

        List<EnrichedTrade> result = tradeEnrichmentService.enrichTradesFromCsv(inputStream);

        assertNotNull(result);
        assertEquals(1, result.size());

        EnrichedTrade expected = EnrichedTrade.toEnrichedTrade(new Trade("2020-01-01", 1L, "Product 1", "USD", 100.50));
        assertEquals(expected, result.get(0));
    }

    @Test
    void testConvertToCSV() {
        List<EnrichedTrade> trades = List.of(
                new EnrichedTrade("2020-01-01", "Product 1", "USD", 100.50),
                new EnrichedTrade("2020-01-02", "Product 2", "EUR", 200.75)
        );

        ByteArrayInputStream csvStream = tradeEnrichmentService.convertToCSV(trades);
        String csvOutput = new BufferedReader(new InputStreamReader(csvStream))
                .lines()
                .collect(Collectors.joining("\n"));

        assertTrue(csvOutput.contains("date,productName,currency,price"));
        assertTrue(csvOutput.contains("2020-01-01,Product 1,USD,100.5"));
        assertTrue(csvOutput.contains("2020-01-02,Product 2,EUR,200.75"));
    }
}