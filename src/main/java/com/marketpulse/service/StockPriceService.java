// src/main/java/com/marketpulse/service/StockPriceService.java

package com.marketpulse.service;

import com.marketpulse.model.StockSnapshot;
import com.marketpulse.repository.StockSnapshotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class StockPriceService {

    @Value("${alphavantage.api.key}")
    private String apiKey;

    private final StockSnapshotRepository repository;
    private final RestTemplate restTemplate;

    public StockPriceService(StockSnapshotRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    public StockSnapshot fetchAndSave(String ticker) {
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
                + ticker + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("Global Quote")) {
            throw new RuntimeException("No data returned for ticker: " + ticker);
        }

        Map<String, String> quote = (Map<String, String>) response.get("Global Quote");

        if (quote == null || quote.isEmpty()) {
            throw new RuntimeException("Empty quote data for ticker: " + ticker
                + ". Free tier may be rate-limited — try again in a minute.");
        }

        double price  = Double.parseDouble(quote.get("05. price"));
        double high   = Double.parseDouble(quote.get("03. high"));
        double low    = Double.parseDouble(quote.get("04. low"));
        long   volume = Long.parseLong(quote.get("06. volume"));

        StockSnapshot snapshot = new StockSnapshot(ticker.toUpperCase(), price, high, low, volume);
        return repository.save(snapshot);
    }
}