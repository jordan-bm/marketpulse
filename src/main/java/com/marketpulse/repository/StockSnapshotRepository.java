// src/main/java/com/marketpulse/repository/StockSnapshotRepository.java

package com.marketpulse.repository;

import com.marketpulse.model.StockSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface StockSnapshotRepository extends MongoRepository<StockSnapshot, String> {
    List<StockSnapshot> findByTickerOrderByTimestampDesc(String ticker);
}