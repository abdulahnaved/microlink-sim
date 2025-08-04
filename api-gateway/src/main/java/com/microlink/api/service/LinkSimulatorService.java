package com.microlink.api.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microlink.api.model.LinkMetrics;

import reactor.core.publisher.Mono;


@Service
public class LinkSimulatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkSimulatorService.class);
    
    @Value("${link.simulator.url:http://localhost:8082}")
    private String linkSimulatorUrl;
    
    @Value("${link.simulator.command:./link-sim/link_sim.exe}")
    private String linkSimulatorCommand;
    
    @Value("${link.simulator.timeout:5000}")
    private int timeoutMs;
    
    @Value("${link.simulator.mode:process}")
    private String simulatorMode; // "process" or "http"
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    
    public LinkSimulatorService() {
        // Initialize HttpClient with a default timeout to avoid PT0S error
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(5000))
                .build();
    }
    
    /**
     * Fetch current metrics from the C link simulator
     * @return LinkMetrics object with current simulation data
     */
    public Mono<LinkMetrics> getCurrentMetrics() {
        return Mono.fromCallable(() -> {
            try {
                if ("http".equals(simulatorMode)) {
                    return getMetricsViaHttp();
                } else {
                    return getMetricsViaProcess();
                }
            } catch (Exception e) {
                logger.error("Error getting metrics from simulator: {}", e.getMessage());
                // Fallback to mock data
                return getMockMetrics().block();
            }
        });
    }
    
    /**
     * Get metrics via HTTP request
     */
    private LinkMetrics getMetricsViaHttp() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(linkSimulatorUrl + "/metrics"))
                .timeout(Duration.ofMillis(timeoutMs))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            logger.error("Link simulator returned status code: {}", response.statusCode());
            throw new RuntimeException("Link simulator failed with status code: " + response.statusCode());
        }
        
        String json = response.body().trim();
        logger.debug("Received JSON from link simulator: {}", json);
        
        return objectMapper.readValue(json, LinkMetrics.class);
    }
    
    /**
     * Get metrics via local process execution
     */
    private LinkMetrics getMetricsViaProcess() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(linkSimulatorCommand, "--json");
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        // Read the JSON output
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            
            StringBuilder jsonOutput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonOutput.append(line).append("\n");
            }
            
            // Wait for process to complete
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                logger.error("Link simulator process exited with code: {}", exitCode);
                throw new RuntimeException("Link simulator failed with exit code: " + exitCode);
            }
            
            // Extract only the JSON part from the output
            String fullOutput = jsonOutput.toString().trim();
            String json = extractJsonFromOutput(fullOutput);
            
            logger.debug("Received JSON from link simulator: {}", json);
            
            return objectMapper.readValue(json, LinkMetrics.class);
        }
    }
    
    /**
     * Extract JSON from C simulator output that may contain initialization messages
     */
    private String extractJsonFromOutput(String fullOutput) {
        // Find the first occurrence of '{' and the last occurrence of '}'
        int startIndex = fullOutput.indexOf('{');
        int endIndex = fullOutput.lastIndexOf('}');
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return fullOutput.substring(startIndex, endIndex + 1);
        } else {
            throw new RuntimeException("Could not extract JSON from simulator output: " + fullOutput);
        }
    }
    
    /**
     * Generate mock metrics for testing when C simulator is not available
     * @return LinkMetrics object with mock data
     */
    public Mono<LinkMetrics> getMockMetrics() {
        return Mono.fromCallable(() -> {
            long timestamp = System.currentTimeMillis() / 1000; // Unix timestamp
            
            return new LinkMetrics(
                15.0 + Math.random() * 4.0,      // Latency: 15-19ms
                0.1 + Math.random() * 4.9,       // Jitter: 0.1-5ms
                -85.0 + Math.random() * 40.0,    // Signal strength: -85 to -45 dBm
                Math.random() * 2.0,              // Packet loss: 0-2%
                50.0 + Math.random() * 950.0,    // Bandwidth: 50-1000 Mbps
                -75.0 + Math.random() * 20.0,    // SNR: -75 to -55 dB
                timestamp
            );
        });
    }
    
    /**
     * Check if the link simulator is available
     * @return true if simulator is accessible
     */
    public boolean isSimulatorAvailable() {
        try {
            if ("http".equals(simulatorMode)) {
                return isSimulatorAvailableViaHttp();
            } else {
                return isSimulatorAvailableViaProcess();
            }
        } catch (Exception e) {
            logger.warn("Link simulator not available: {}", e.getMessage());
            return false;
        }
    }
    
    private boolean isSimulatorAvailableViaHttp() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(linkSimulatorUrl + "/health"))
                .timeout(Duration.ofMillis(2000))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        return response.statusCode() == 200;
    }
    
    private boolean isSimulatorAvailableViaProcess() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(linkSimulatorCommand, "--json");
        Process process = processBuilder.start();
        
        // Wait for a short time to see if it starts
        boolean completed = process.waitFor() == 0;
        return completed && process.exitValue() == 0;
    }
} 