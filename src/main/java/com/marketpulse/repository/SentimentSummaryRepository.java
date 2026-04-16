// src/main/java/com/marketpulse/repository/SentimentSummaryRepository.java

package com.marketpulse.repository;

import com.marketpulse.model.SentimentSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface SentimentSummaryRepository extends MongoRepository<SentimentSummary, String> {
    Optional<SentimentSummary> findTopByTickerOrderByTimestampDesc(String ticker);
}