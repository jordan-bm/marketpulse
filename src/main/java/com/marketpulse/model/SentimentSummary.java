// src/main/java/com/marketpulse/model/SentimentSummary.java

package com.marketpulse.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "sentiment_summaries")
public class SentimentSummary {

    @Id
    private String id;

    @Indexed
    private String ticker;

    private double averageSentiment;
    private int articleCount;
    private int bullishCount;
    private int bearishCount;
    private int neutralCount;
    private String recommendation;
    private LocalDateTime timestamp;

    public SentimentSummary() {}

    public SentimentSummary(String ticker, double averageSentiment, int articleCount,
                             int bullishCount, int bearishCount, int neutralCount,
                             String recommendation) {
        this.ticker = ticker;
        this.averageSentiment = averageSentiment;
        this.articleCount = articleCount;
        this.bullishCount = bullishCount;
        this.bearishCount = bearishCount;
        this.neutralCount = neutralCount;
        this.recommendation = recommendation;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getAverageSentiment() { return averageSentiment; }
    public void setAverageSentiment(double averageSentiment) { this.averageSentiment = averageSentiment; }

    public int getArticleCount() { return articleCount; }
    public void setArticleCount(int articleCount) { this.articleCount = articleCount; }

    public int getBullishCount() { return bullishCount; }
    public void setBullishCount(int bullishCount) { this.bullishCount = bullishCount; }

    public int getBearishCount() { return bearishCount; }
    public void setBearishCount(int bearishCount) { this.bearishCount = bearishCount; }

    public int getNeutralCount() { return neutralCount; }
    public void setNeutralCount(int neutralCount) { this.neutralCount = neutralCount; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}