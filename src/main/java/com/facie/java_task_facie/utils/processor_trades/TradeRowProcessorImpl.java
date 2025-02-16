package com.facie.java_task_facie.utils.processor_trades;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.facie.java_task_facie.models.Trade;
import com.facie.java_task_facie.service.ProductService;
import com.facie.java_task_facie.service.TradeEnrichmentService;
import com.facie.java_task_facie.utils.function.ProcessingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TradeRowProcessorImpl implements TradeRowProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TradeRowProcessorImpl.class);
    private final ProductService productService;
    private final ProcessingData processingData;

    public TradeRowProcessorImpl(ProductService productService) {
        this.productService = productService;
        this.processingData = new ProcessingData(logger);
    }

    @Override
    public Optional<Trade> processRow(String[] row, AtomicInteger discardedRows) {
        if (row.length < 4) {
            logger.error("Invalid CSV row, fewer than 4 columns: {}", String.join(",", row));
            discardedRows.incrementAndGet();
            return Optional.empty();
        }

        List<String> errors = new ArrayList<>();
        String date = row[0];

        if (TradeEnrichmentService.isValidDate(date)) errors.add("Invalid date format: " + date);
        Long productId = processingData.parseLong(row[1], "Invalid product ID", errors);
        Double price = processingData.parseDouble(row[3], "Invalid price", errors);

        if (!errors.isEmpty()) {
            processingData.logAndDiscard(discardedRows, String.join("; ", errors));
            return Optional.empty();
        }

        String productName = productService.getProductName(productId);
        return Optional.of(new Trade(date, productId, productName, row[2], price));
    }
}
