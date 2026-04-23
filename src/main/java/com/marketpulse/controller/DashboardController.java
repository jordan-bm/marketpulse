// src/main/java/com/marketpulse/controller/DashboardController.java

package com.marketpulse.controller;

import com.marketpulse.model.NewsArticle;
import com.marketpulse.model.SentimentSummary;
import com.marketpulse.model.StockSnapshot;
import com.marketpulse.repository.SentimentSummaryRepository;
import com.marketpulse.service.NewsService;
import com.marketpulse.service.SentimentService;
import com.marketpulse.service.StockPriceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    private final StockPriceService stockPriceService;
    private final NewsService newsService;
    private final SentimentService sentimentService;
    private final SentimentSummaryRepository sentimentSummaryRepository;

    public DashboardController(StockPriceService stockPriceService,
                                NewsService newsService,
                                SentimentService sentimentService,
                                SentimentSummaryRepository sentimentSummaryRepository) {
        this.stockPriceService = stockPriceService;
        this.newsService = newsService;
        this.sentimentService = sentimentService;
        this.sentimentSummaryRepository = sentimentSummaryRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String ticker, Model model) {
        if (ticker == null || ticker.isBlank()) {
            return "dashboard";
        }

        ticker = ticker.trim().toUpperCase();
        model.addAttribute("ticker", ticker);

        try {
            StockSnapshot snapshot = stockPriceService.fetchAndSave(ticker);
            model.addAttribute("snapshot", snapshot);
        } catch (Exception e) {
            model.addAttribute("error", "Could not fetch price data for " + ticker + ": " + e.getMessage());
            return "dashboard";
        }

        try {
            List<NewsArticle> articles = newsService.fetchAndSave(ticker);
            if (articles.isEmpty()) articles = newsService.getLatest(ticker);
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            model.addAttribute("articles", List.of());
        }

        return "dashboard";
    }

    @GetMapping("/ticker/{symbol}")
    public String tickerDeepDive(@PathVariable String symbol, Model model) {
        symbol = symbol.trim().toUpperCase();
        model.addAttribute("symbol", symbol);

        // Price
        try {
            StockSnapshot snapshot = stockPriceService.fetchAndSave(symbol);
            model.addAttribute("snapshot", snapshot);
        } catch (Exception e) {
            model.addAttribute("error", "Could not fetch price data for " + symbol);
        }

        // Sentiment summary + recommendation panel class
        SentimentSummary summary = sentimentSummaryRepository
            .findTopByTickerOrderByTimestampDesc(symbol);
        if (summary != null) {
            model.addAttribute("summary", summary);
            model.addAttribute("recPanelClass", resolvePanelClass(summary.getAverageSentiment()));
        }

        // Scored headlines
        try {
            List<NewsArticle> articles = newsService.fetchAndSave(symbol);
            if (articles.isEmpty()) articles = newsService.getLatest(symbol);
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            model.addAttribute("articles", List.of());
        }

        return "ticker";
    }

    private String resolvePanelClass(double score) {
        if (score >= 0.6)  return "bullish-strong";
        if (score >= 0.3)  return "bullish-lean";
        if (score <= -0.6) return "bearish-strong";
        if (score <= -0.3) return "bearish-lean";
        return "neutral";
    }
}