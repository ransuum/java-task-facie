package com.facie.java_task_facie.service;

import java.util.Map;

public interface ProductService {
    String getProductName(Long productId);
    Map<Long, String> getAllProducts();
}
