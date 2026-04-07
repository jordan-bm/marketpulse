// src/main/java/com/marketpulse/controller/ApiController.java

package com.marketpulse.controller;

import com.marketpulse.model.NewsArticle;
import com.marketpulse.model.StockSnapshot;
import com.marketpulse.service.NewsService;
import com.marketpulse.service.StockPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    private final StockPriceService stockPriceService;
    private final NewsService newsService;

    public ApiController(StockPriceService stockPriceService, NewsService newsService) {
        this.stockPriceService = stockPriceService;
        this.newsService = newsService;
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

    @GetMapping("/api/news/{ticker}")
    public ResponseEntity<?> getNews(@PathVariable String ticker) {
        try {
            List<NewsArticle> articles = newsService.fetchAndSave(ticker.toUpperCase());
            if (articles.isEmpty()) {
                // May already be in DB from a prior call — return what we have
                return ResponseEntity.ok(newsService.getLatest(ticker));
            }
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}