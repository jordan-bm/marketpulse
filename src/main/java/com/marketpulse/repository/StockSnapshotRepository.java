// src/main/java/com/marketpulse/repository/StockSnapshotRepository.java

package com.marketpulse.repository;

import com.marketpulse.model.StockSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockSnapshotRepository extends MongoRepository<StockSnapshot, String> {
    StockSnapshot findTopByTickerOrderByTimestampDesc(String ticker);
    List<StockSnapshot> findTop20ByTickerOrderByTimestampDesc(String ticker);
}