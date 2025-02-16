package com.facie.java_task_facie.controller;

import com.facie.java_task_facie.models.EnrichedTrade;
import com.facie.java_task_facie.service.impl.TradeEnrichmentServiceImpl;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TradeController {
    private final TradeEnrichmentServiceImpl enrichmentService;

    public TradeController(TradeEnrichmentServiceImpl enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @PostMapping(value = "/enrich", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<InputStreamResource> enrichTrades(@RequestBody InputStream csvData) {
        List<EnrichedTrade> enrichedTrades = enrichmentService.enrichTradesFromCsv(csvData);
        InputStream resultStream = enrichmentService.convertToCSV(enrichedTrades);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=enriched_trades.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(resultStream));
    }

    @PostMapping(value = "/enrich", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InputStreamResource> enrichTradesMultipart(@RequestParam("file") MultipartFile file) {
        try {
            List<EnrichedTrade> enrichedTrades = enrichmentService.enrichTradesFromCsv(file.getInputStream());
            InputStream resultStream = enrichmentService.convertToCSV(enrichedTrades);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getOriginalFilename());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(new InputStreamResource(resultStream));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
