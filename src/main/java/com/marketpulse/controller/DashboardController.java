// src/main/java/com/marketpulse/controller/DashboardController.java

package com.marketpulse.controller;

import com.marketpulse.model.NewsArticle;
import com.marketpulse.model.StockSnapshot;
import com.marketpulse.service.NewsService;
import com.marketpulse.service.StockPriceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    private final StockPriceService stockPriceService;
    private final NewsService newsService;

    public DashboardController(StockPriceService stockPriceService, NewsService newsService) {
        this.stockPriceService = stockPriceService;
        this.newsService = newsService;
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
            if (articles.isEmpty()) {
                articles = newsService.getLatest(ticker);
            }
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            // News failure shouldn't kill the whole page — just show empty
            model.addAttribute("articles", List.of());
        }

        return "dashboard";
    }
}