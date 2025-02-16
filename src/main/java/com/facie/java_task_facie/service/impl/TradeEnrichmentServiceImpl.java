package com.facie.java_task_facie.service.impl;

import com.facie.java_task_facie.models.EnrichedTrade;
import com.facie.java_task_facie.models.Trade;
import com.facie.java_task_facie.service.TradeEnrichmentService;
import com.facie.java_task_facie.utils.processor_trades.TradeRowProcessor;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class TradeEnrichmentServiceImpl implements TradeEnrichmentService {
    private static final Logger logger = LoggerFactory.getLogger(TradeEnrichmentServiceImpl.class);
    private final TradeRowProcessor tradeRowProcessor;
    private final ExecutorService executor;

    public TradeEnrichmentServiceImpl(TradeRowProcessor tradeRowProcessor) {
        this.tradeRowProcessor = tradeRowProcessor;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public List<EnrichedTrade> enrichTradesFromCsv(InputStream csvInputStream) {
        List<EnrichedTrade> enrichedTrades = new ArrayList<>();
        AtomicInteger discardedRows = new AtomicInteger(0);

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(csvInputStream)).withSkipLines(1).build()) {

            List<CompletableFuture<Optional<Trade>>> futures = reader.readAll().stream()
                    .map(row -> CompletableFuture.supplyAsync(() ->
                            tradeRowProcessor.processRow(row, discardedRows), executor))
                    .toList();

            CompletableFuture<List<Optional<Trade>>> resultsFuture = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])).thenApply(v ->
                    futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));

            enrichedTrades = resultsFuture.join()
                    .stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(EnrichedTrade::toEnrichedTrade)
                    .collect(Collectors.toList());

        } catch (IOException | CsvException e) {
            logger.error("Error reading trade CSV file", e);
        }

        logger.info("Processed trades. Enriched: {}, Discarded: {}", enrichedTrades.size(), discardedRows.get());
        return enrichedTrades;
    }

    @Override
    public ByteArrayInputStream convertToCSV(List<EnrichedTrade> trades) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            writer.writeNext(CSV_HEADER, false);

            trades.forEach(trade -> {
                String[] record = {
                        trade.date(),
                        trade.productName(),
                        trade.currency(),
                        String.valueOf(trade.price())
                };
                writer.writeNext(record, false);
            });
        } catch (IOException e) {
            logger.error("Error writing trades to CSV", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
    }
}
