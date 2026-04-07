// src/main/java/com/marketpulse/model/NewsArticle.java

package com.marketpulse.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "news_articles")
public class NewsArticle {

    @Id
    private String id;
    private String ticker;
    private String headline;
    private String source;
    private String url;
    private LocalDateTime timestamp;
    private Double sentimentScore; // null for now — wired in Phase 2

    public NewsArticle() {}

    public NewsArticle(String ticker, String headline, String source, String url, LocalDateTime timestamp) {
        this.ticker = ticker;
        this.headline = headline;
        this.source = source;
        this.url = url;
        this.timestamp = timestamp;
        this.sentimentScore = null;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }
}