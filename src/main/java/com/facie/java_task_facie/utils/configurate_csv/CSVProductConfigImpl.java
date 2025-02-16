package com.facie.java_task_facie.utils.configurate_csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class CSVProductConfigImpl implements CSVProductConfig {
    private static final Logger log = LoggerFactory.getLogger(CSVProductConfigImpl.class);

    @Override
    public void loadProductsFromCsv(Map<Long, String> productCache) throws IOException, CsvException {
        ClassPathResource resource = new ClassPathResource("product.csv");
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .withSkipLines(1)
                .build()) {

            reader.readAll().forEach(row -> {
                try {
                    long productId = Long.parseLong(row[0]);
                    productCache.put(productId, row[1]);
                } catch (NumberFormatException e) {
                    log.error("Invalid product ID in CSV: {}", row[0], e);
                }
            });
        }
        log.info("Loaded {} products into cache", productCache.size());
    }
}
