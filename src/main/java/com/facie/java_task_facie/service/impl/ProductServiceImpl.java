package com.facie.java_task_facie.service.impl;

import com.facie.java_task_facie.enums.ErrorType;
import com.facie.java_task_facie.service.ProductService;
import com.facie.java_task_facie.utils.configurate_csv.CSVProductConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final Map<Long, String> productCache;
    private final CSVProductConfig csvProductConfig;

    public ProductServiceImpl(CSVProductConfig csvProductConfig) {
        this.csvProductConfig = csvProductConfig;
        this.productCache = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void initialize() {
        try {
            // Load products from CSV into the local cache.
            this.csvProductConfig.loadProductsFromCsv(this.productCache);
        } catch (Exception e) {
            log.error("Failed to load products from CSV", e);
        }
    }

    @Override
    @Cacheable(value = "productNames")
    public String getProductName(Long productId) {
        String productName = productCache.get(productId);
        if (productName == null) {
            log.warn("Product name not found for product ID: {}", productId);
            return ErrorType.MISSING_PRODUCT_NAME.val;
        }
        return productName;
    }

    @Override
    public Map<Long, String> getAllProducts() {
        return new HashMap<>(productCache);
    }
}
