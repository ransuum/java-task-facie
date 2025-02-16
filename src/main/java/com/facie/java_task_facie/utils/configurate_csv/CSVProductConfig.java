package com.facie.java_task_facie.utils.configurate_csv;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.Map;

@FunctionalInterface
public interface CSVProductConfig {
    void loadProductsFromCsv(Map<Long, String> productCache) throws IOException, CsvException;
}
