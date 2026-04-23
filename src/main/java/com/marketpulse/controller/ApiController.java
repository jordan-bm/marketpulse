// src/main/java/com/marketpulse/controller/ApiController.java

package com.marketpulse.controller;

import com.marketpulse.model.NewsArticle;
import com.marketpulse.model.SentimentSummary;
import com.marketpulse.model.StockSnapshot;
import com.marketpulse.repository.NewsArticleRepository;
import com.marketpulse.repository.SentimentSummaryRepository;
import com.marketpulse.repository.StockSnapshotRepository;
import com.marketpulse.service.SentimentService;
import com.marketpulse.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired private StockSnapshotRepository stockSnapshotRepository;
    @Autowired private NewsArticleRepository newsArticleRepository;
    @Autowired private SentimentSummaryRepository sentimentSummaryRepository;
    @Autowired private SentimentService sentimentService;
    @Autowired private SchedulerService schedulerService;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "version", "1.0");
    }

    @GetMapping("/stock/{ticker}")
    public ResponseEntity<?> getStock(@PathVariable String ticker) {
        try {
            StockSnapshot snapshot = stockSnapshotRepository
                .findTopByTickerOrderByTimestampDesc(ticker.toUpperCase());
            if (snapshot == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(snapshot);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/news/{ticker}")
    public ResponseEntity<?> getNews(@PathVariable String ticker) {
        List<NewsArticle> articles = newsArticleRepository
            .findTop10ByTickerOrderByTimestampDesc(ticker.toUpperCase());
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/sentiment/test")
    public Map<String, Object> testSentiment(@RequestParam String text) {
        double score = sentimentService.scoreSentiment(text);
        String label;
        if (score >= 0.6) label = "Very Positive";
        else if (score >= 0.3) label = "Positive";
        else if (score <= -0.6) label = "Very Negative";
        else if (score <= -0.3) label = "Negative";
        else label = "Neutral";
        return Map.of("score", score, "label", label, "text", text);
    }

    @GetMapping("/sentiment/{ticker}")
    public ResponseEntity<?> getSentiment(@PathVariable String ticker) {
        SentimentSummary summary = sentimentSummaryRepository
            .findTopByTickerOrderByTimestampDesc(ticker.toUpperCase());
        if (summary == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/sentiment/backfill")
    public Map<String, Object> backfill() {
        int count = sentimentService.backfillNullScores();
        return Map.of("backfilled", count);
    }

    @GetMapping("/scheduler/status")
    public Map<String, Object> schedulerStatus() {
        return Map.of(
            "lastRun", schedulerService.getLastRun() != null
                ? schedulerService.getLastRun().toString() : "Not yet run",
            "nextRun", schedulerService.getNextRun() != null
                ? schedulerService.getNextRun().toString() : "Pending"
        );
    }

    // ── STEP 10 ── Chart data endpoint ──────────────────────────────────────
    @GetMapping("/chart/{ticker}")
    public ResponseEntity<?> getChartData(@PathVariable String ticker) {
        String t = ticker.toUpperCase();

        // Last 20 price snapshots (oldest → newest for chart order)
        List<StockSnapshot> snapshots = stockSnapshotRepository
            .findTop20ByTickerOrderByTimestampDesc(t);
        Collections.reverse(snapshots);

        // Last 20 sentiment summaries (oldest → newest)
        List<SentimentSummary> summaries = sentimentSummaryRepository
            .findTop20ByTickerOrderByTimestampDesc(t);
        Collections.reverse(summaries);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd HH:mm");

        List<String> priceLabels = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        for (StockSnapshot s : snapshots) {
            priceLabels.add(s.getTimestamp().format(fmt));
            prices.add(s.getPrice());
        }

        List<String> sentimentLabels = new ArrayList<>();
        List<Double> sentimentScores = new ArrayList<>();
        for (SentimentSummary s : summaries) {
            sentimentLabels.add(s.getTimestamp().format(fmt));
            sentimentScores.add(s.getAverageSentiment());
        }

        // Current sentiment summary for the gauge
        SentimentSummary latest = sentimentSummaryRepository
            .findTopByTickerOrderByTimestampDesc(t);

        Map<String, Object> result = new HashMap<>();
        result.put("priceLabels", priceLabels);
        result.put("prices", prices);
        result.put("sentimentLabels", sentimentLabels);
        result.put("sentimentScores", sentimentScores);
        result.put("recommendation", latest != null ? latest.getRecommendation() : "⏸ Hold — Sentiment Neutral");
        result.put("averageSentiment", latest != null ? latest.getAverageSentiment() : 0.0);

        return ResponseEntity.ok(result);
    }
}