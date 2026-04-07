// src/main/java/com/marketpulse/service/NewsService.java

package com.marketpulse.service;

import com.marketpulse.model.NewsArticle;
import com.marketpulse.repository.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NewsService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private final NewsArticleRepository repository;
    private final RestTemplate restTemplate;

    public NewsService(NewsArticleRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    public List<NewsArticle> fetchAndSave(String ticker) {
        // Finnhub expects a date range — we'll grab the last 7 days
        long now  = Instant.now().getEpochSecond();
        long week = now - (7L * 24 * 60 * 60);

        String url = "https://finnhub.io/api/v1/company-news?symbol=" + ticker
                + "&from=" + toDateString(week)
                + "&to="   + toDateString(now)
                + "&token=" + apiKey;

        List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

        if (response == null || response.isEmpty()) {
            return List.of();
        }

        List<NewsArticle> saved = new ArrayList<>();

        for (Map<String, Object> item : response) {
            String headline = (String) item.get("headline");
            String source   = (String) item.get("source");
            String articleUrl = (String) item.get("url");
            long   epochSec = ((Number) item.get("datetime")).longValue();

            LocalDateTime timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(epochSec), ZoneId.systemDefault()
            );

            // Deduplication check
            if (repository.existsByTickerAndHeadline(ticker.toUpperCase(), headline)) {
                continue;
            }

            NewsArticle article = new NewsArticle(
                ticker.toUpperCase(), headline, source, articleUrl, timestamp
            );
            saved.add(repository.save(article));

            // Stop after 10 new articles
            if (saved.size() >= 10) break;
        }

        return saved;
    }

    public List<NewsArticle> getLatest(String ticker) {
        List<NewsArticle> all = repository.findByTickerOrderByTimestampDesc(ticker.toUpperCase());
        return all.size() > 10 ? all.subList(0, 10) : all;
    }

    private String toDateString(long epochSeconds) {
        LocalDateTime dt = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault()
        );
        return dt.toLocalDate().toString(); // yields "YYYY-MM-DD"
    }
}