// src/main/java/com/marketpulse/model/StockSnapshot.java

package com.marketpulse.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "stock_snapshots")
public class StockSnapshot {

    @Id
    private String id;
    private String ticker;
    private double price;
    private double high;
    private double low;
    private long volume;
    private LocalDateTime timestamp;

    public StockSnapshot() {}

    public StockSnapshot(String ticker, double price, double high, double low, long volume) {
        this.ticker = ticker;
        this.price = price;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }

    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}