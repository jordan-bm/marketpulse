// src/main/java/com/marketpulse/controller/ApiController.java

package com.marketpulse.controller;

import com.marketpulse.model.StockSnapshot;
import com.marketpulse.service.StockPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ApiController {

    private final StockPriceService stockPriceService;

    public ApiController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "version", "1.0");
    }

    @GetMapping("/api/stock/{ticker}")
    public ResponseEntity<?> getStock(@PathVariable String ticker) {
        try {
            StockSnapshot snapshot = stockPriceService.fetchAndSave(ticker.toUpperCase());
            return ResponseEntity.ok(snapshot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}