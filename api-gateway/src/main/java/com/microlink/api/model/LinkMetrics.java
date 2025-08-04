package com.microlink.api.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LinkMetrics {
    
    @JsonProperty("latency_ms")
    @NotNull
    private Double latencyMs;
    
    @JsonProperty("jitter_ms")
    @NotNull
    private Double jitterMs;
    
    @JsonProperty("signal_strength_db")
    @NotNull
    private Double signalStrengthDb;
    
    @JsonProperty("packet_loss_rate")
    @NotNull
    private Double packetLossRate;
    
    @JsonProperty("bandwidth_mbps")
    @NotNull
    private Double bandwidthMbps;
    
    @JsonProperty("snr_db")
    @NotNull
    private Double snrDb;
    
    @JsonProperty("timestamp")
    @NotNull
    private Long timestamp;
    
    // Default constructor
    public LinkMetrics() {}
    
    // Constructor with all fields
    public LinkMetrics(Double latencyMs, Double jitterMs, Double signalStrengthDb, 
                      Double packetLossRate, Double bandwidthMbps, Double snrDb, Long timestamp) {
        this.latencyMs = latencyMs;
        this.jitterMs = jitterMs;
        this.signalStrengthDb = signalStrengthDb;
        this.packetLossRate = packetLossRate;
        this.bandwidthMbps = bandwidthMbps;
        this.snrDb = snrDb;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public Double getLatencyMs() {
        return latencyMs;
    }
    
    public void setLatencyMs(Double latencyMs) {
        this.latencyMs = latencyMs;
    }
    
    public Double getJitterMs() {
        return jitterMs;
    }
    
    public void setJitterMs(Double jitterMs) {
        this.jitterMs = jitterMs;
    }
    
    public Double getSignalStrengthDb() {
        return signalStrengthDb;
    }
    
    public void setSignalStrengthDb(Double signalStrengthDb) {
        this.signalStrengthDb = signalStrengthDb;
    }
    
    public Double getPacketLossRate() {
        return packetLossRate;
    }
    
    public void setPacketLossRate(Double packetLossRate) {
        this.packetLossRate = packetLossRate;
    }
    
    public Double getBandwidthMbps() {
        return bandwidthMbps;
    }
    
    public void setBandwidthMbps(Double bandwidthMbps) {
        this.bandwidthMbps = bandwidthMbps;
    }
    
    public Double getSnrDb() {
        return snrDb;
    }
    
    public void setSnrDb(Double snrDb) {
        this.snrDb = snrDb;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "LinkMetrics{" +
                "latencyMs=" + latencyMs +
                ", jitterMs=" + jitterMs +
                ", signalStrengthDb=" + signalStrengthDb +
                ", packetLossRate=" + packetLossRate +
                ", bandwidthMbps=" + bandwidthMbps +
                ", snrDb=" + snrDb +
                ", timestamp=" + timestamp +
                '}';
    }
} 