// src/main/java/com/marketpulse/service/SentimentService.java

package com.marketpulse.service;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class SentimentService {

    private StanfordCoreNLP pipeline;

    // Positive financial/market terms
    private static final Set<String> POSITIVE_WORDS = new HashSet<>(Arrays.asList(
        "beat", "beats", "exceeded", "exceeds", "surpassed", "record", "high", "growth",
        "profit", "profits", "gain", "gains", "rally", "rallied", "surge", "surged",
        "soar", "soared", "rise", "rises", "rose", "up", "upgrade", "upgraded",
        "outperform", "outperformed", "strong", "stronger", "strongest", "positive",
        "bullish", "boom", "booming", "recovery", "recovered", "breakthrough",
        "opportunity", "opportunities", "optimistic", "confidence", "confident",
        "earnings", "revenue", "dividend", "dividends", "buyback", "innovation",
        "partnership", "deal", "deals", "acquisition", "approved", "launch", "launched"
    ));

    // Negative financial/market terms
    private static final Set<String> NEGATIVE_WORDS = new HashSet<>(Arrays.asList(
        "crash", "crashes", "crashed", "fell", "fall", "falls", "drop", "drops", "dropped",
        "plunge", "plunged", "decline", "declines", "declined", "loss", "losses", "miss",
        "missed", "below", "weak", "weaker", "weakest", "negative", "bearish", "bear",
        "recession", "fear", "fears", "risk", "risks", "concern", "concerns", "warning",
        "warns", "downgrade", "downgraded", "underperform", "disappoints", "disappointed",
        "disappointing", "lawsuit", "investigation", "fraud", "scandal", "bankruptcy",
        "layoffs", "layoff", "cut", "cuts", "shortage", "deficit", "debt", "inflation",
        "tariff", "tariffs", "sanction", "sanctions", "volatile", "volatility", "sell"
    ));

    // Negation words that flip the score
    private static final Set<String> NEGATION_WORDS = new HashSet<>(Arrays.asList(
        "not", "no", "never", "neither", "nor", "barely", "hardly", "scarcely", "doesn't",
        "don't", "didn't", "won't", "wasn't", "aren't", "isn't", "can't", "cannot"
    ));

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        this.pipeline = new StanfordCoreNLP(props);
    }

    /**
     * Scores a text string for sentiment using a financial lexicon.
     * Returns a double from -1.0 (very negative) to +1.0 (very positive).
     */
    public double scoreSentiment(String text) {
        if (text == null || text.isBlank()) return 0.0;

        Annotation annotation = pipeline.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        if (sentences == null || sentences.isEmpty()) return 0.0;

        double total = 0.0;
        int count = 0;

        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            total += scoreSentence(tokens);
            count++;
        }

        double raw = count > 0 ? total / count : 0.0;
        // Clamp to [-1.0, 1.0]
        return Math.max(-1.0, Math.min(1.0, raw));
    }

    private double scoreSentence(List<CoreLabel> tokens) {
        double score = 0.0;
        boolean negated = false;

        for (int i = 0; i < tokens.size(); i++) {
            String word = tokens.get(i).word().toLowerCase();

            if (NEGATION_WORDS.contains(word)) {
                negated = true;
                continue;
            }

            double wordScore = 0.0;
            if (POSITIVE_WORDS.contains(word)) wordScore = 0.5;
            else if (NEGATIVE_WORDS.contains(word)) wordScore = -0.5;

            if (negated && wordScore != 0.0) {
                wordScore = -wordScore;
                negated = false;
            }

            score += wordScore;
        }

        return score;
    }

    /**
     * Maps a numeric sentiment score to a human-readable investment recommendation.
     */
    public String mapToRecommendation(double score) {
        if (score >= 0.6)  return "🟢 Strong Buy Signal";
        if (score >= 0.3)  return "📈 Leaning Bullish";
        if (score > -0.3)  return "⏸ Hold — Sentiment Neutral";
        if (score > -0.6)  return "⚠️ Caution — Bearish Pressure";
        return "🔴 Strong Sell Signal";
    }
}