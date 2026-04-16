// src/main/java/com/marketpulse/service/SchedulerService.java

package com.marketpulse.service;

import com.marketpulse.model.SentimentSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private StockPriceService stockPriceService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private SentimentService sentimentService;

    private static final List<String> WATCHLIST = List.of("AAPL", "MSFT", "GOOGL", "TSLA", "AMZN");

    private LocalDateTime lastRun;
    private LocalDateTime nextRun;

    @Scheduled(fixedRate = 900000) // every 15 minutes
    public void refresh() {
        lastRun = LocalDateTime.now();
        nextRun = lastRun.plusMinutes(15);

        int totalArticles = 0;
        double totalSentiment = 0.0;

        for (String ticker : WATCHLIST) {
            try {
                stockPriceService.fetchAndSave(ticker);
                newsService.fetchAndSave(ticker);
                SentimentSummary summary = sentimentService.calculateSummary(ticker);
                if (summary != null) {
                    totalArticles += summary.getArticleCount();
                    totalSentiment += summary.getAverageSentiment();
                }
            } catch (Exception e) {
                System.err.println("[Scheduler] Error refreshing " + ticker + ": " + e.getMessage());
            }
        }

        double avgSentiment = totalSentiment / WATCHLIST.size();
        System.out.printf("[Scheduler] %s | Tickers: %s | Articles: %d | Avg Sentiment: %.3f%n",
            lastRun, WATCHLIST, totalArticles, avgSentiment);
    }

    public LocalDateTime getLastRun() { return lastRun; }
    public LocalDateTime getNextRun() { return nextRun; }
}