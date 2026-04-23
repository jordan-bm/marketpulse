// src/main/java/com/marketpulse/repository/SentimentSummaryRepository.java

package com.marketpulse.repository;

import com.marketpulse.model.SentimentSummary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SentimentSummaryRepository extends MongoRepository<SentimentSummary, String> {
    SentimentSummary findTopByTickerOrderByTimestampDesc(String ticker);
    List<SentimentSummary> findTop20ByTickerOrderByTimestampDesc(String ticker);
}