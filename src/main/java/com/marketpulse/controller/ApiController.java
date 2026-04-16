// src/main/java/com/marketpulse/controller/ApiController.java

package com.marketpulse.controller;

import com.marketpulse.model.NewsArticle;
import com.marketpulse.model.SentimentSummary;
import com.marketpulse.model.StockSnapshot;
import com.marketpulse.service.NewsService;
import com.marketpulse.service.SentimentService;
import com.marketpulse.service.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    private final StockPriceService stockPriceService;
    private final NewsService newsService;

    @Autowired
    private SentimentService sentimentService;

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
                return ResponseEntity.ok(newsService.getLatest(ticker));
            }
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/sentiment/test")
    public Map<String, Object> testSentiment(@RequestParam String text) {
        double score = sentimentService.scoreSentiment(text);
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("score", score);
        result.put("label", interpretScore(score));
        return result;
    }

    @GetMapping("/api/sentiment/{ticker}")
    public ResponseEntity<?> getSentiment(@PathVariable String ticker) {
        try {
            SentimentSummary summary = sentimentService.calculateSummary(ticker.toUpperCase());
            if (summary == null) {
                return ResponseEntity.ok(Map.of("message", "No articles found for " + ticker));
            }
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/sentiment/backfill")
    public ResponseEntity<?> backfill() {
        int count = sentimentService.backfillNullScores();
        return ResponseEntity.ok(Map.of("backfilled", count));
    }

    private String interpretScore(double score) {
        if (score >= 0.6)  return "Very Positive";
        if (score >= 0.2)  return "Positive";
        if (score > -0.2)  return "Neutral";
        if (score > -0.6)  return "Negative";
        return "Very Negative";
    }
}