// src/main/java/com/marketpulse/repository/NewsArticleRepository.java

package com.marketpulse.repository;

import com.marketpulse.model.NewsArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NewsArticleRepository extends MongoRepository<NewsArticle, String> {
    List<NewsArticle> findByTickerOrderByTimestampDesc(String ticker);
    boolean existsByTickerAndHeadline(String ticker, String headline);
    List<NewsArticle> findBySentimentScoreIsNull();
    List<NewsArticle> findTop10ByTickerOrderByTimestampDesc(String ticker);
    List<NewsArticle> findByTickerOrderByTimestampDesc(String ticker, org.springframework.data.domain.Pageable pageable);
}