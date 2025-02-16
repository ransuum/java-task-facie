package com.facie.java_task_facie.service.impl;

import com.facie.java_task_facie.enums.ErrorType;
import com.facie.java_task_facie.utils.configurate_csv.CSVProductConfig;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private CSVProductConfig csvProductConfig;

    @InjectMocks
    private ProductServiceImpl productService;

    private Map<Long, String> testProductCache;

    @BeforeEach
    public void setUp() throws IOException, CsvException {
        testProductCache = new HashMap<>();
        testProductCache.put(1L, "Product One");
        testProductCache.put(2L, "Product Two");

        lenient().doAnswer(invocation -> {
            Map<Long, String> cacheArg = invocation.getArgument(0);
            cacheArg.putAll(testProductCache);
            return null;
        }).when(csvProductConfig).loadProductsFromCsv(any());
    }

    @Test
    public void testInitialize() throws IOException, CsvException {
        productService.initialize();

        verify(csvProductConfig, times(1)).loadProductsFromCsv(any());

        Map<Long, String> result = productService.getAllProducts();
        assertEquals(testProductCache, result);
    }

    @Test
    public void testInitializeWithException() throws IOException, CsvException {
        doThrow(new RuntimeException("CSV load error")).when(csvProductConfig).loadProductsFromCsv(any());

        productService.initialize();

        verify(csvProductConfig, times(1)).loadProductsFromCsv(any());
        assertTrue(productService.getAllProducts().isEmpty());
    }

    @Test
    public void testGetProductNameFound() {
        productService.initialize();
        String productName = productService.getProductName(1L);

        assertEquals("Product One", productName);
    }

    @Test
    public void testGetProductNameNotFound() {
        productService.initialize();
        String productName = productService.getProductName(999L);

        assertEquals(ErrorType.MISSING_PRODUCT_NAME.val, productName);
    }

    @Test
    public void testGetAllProducts() {
        productService.initialize();
        Map<Long, String> result = productService.getAllProducts();

        assertEquals(testProductCache, result);
    }

}