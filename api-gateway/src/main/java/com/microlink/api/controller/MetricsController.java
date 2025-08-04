package com.microlink.api.controller;

import com.microlink.api.model.LinkMetrics;
import com.microlink.api.service.LinkSimulatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class MetricsController {
    
    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);
    
    private final LinkSimulatorService linkSimulatorService;
    
    @Autowired
    public MetricsController(LinkSimulatorService linkSimulatorService) {
        this.linkSimulatorService = linkSimulatorService;
    }
    
    /**
     * GET /metrics - Retrieve current microwave link metrics
     * @return LinkMetrics object with current simulation data
     */
    @GetMapping("/metrics")
    public Mono<ResponseEntity<LinkMetrics>> getMetrics() {
        logger.info("Received request for link metrics");
        
        return linkSimulatorService.getCurrentMetrics()
                .map(metrics -> {
                    logger.debug("Retrieved metrics: {}", metrics);
                    return ResponseEntity.ok(metrics);
                })
                .onErrorResume(e -> {
                    logger.warn("Failed to get metrics from simulator, using mock data: {}", e.getMessage());
                    return linkSimulatorService.getMockMetrics()
                            .map(ResponseEntity::ok);
                });
    }
    
    /**
     * GET /metrics/health - Health check endpoint
     * @return Health status information
     */
    @GetMapping("/metrics/health")
    public Mono<ResponseEntity<Map<String, Object>>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        
        boolean simulatorAvailable = linkSimulatorService.isSimulatorAvailable();
        health.put("status", simulatorAvailable ? "healthy" : "degraded");
        health.put("simulator_available", simulatorAvailable);
        health.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.ok(health));
    }
    
    /**
     * POST /metrics - Accept external metrics (for future use)
     * @param metrics LinkMetrics object to store
     * @return Confirmation response
     */
    @PostMapping("/metrics")
    public Mono<ResponseEntity<Map<String, String>>> postMetrics(@Valid @RequestBody LinkMetrics metrics) {
        logger.info("Received external metrics: {}", metrics);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "received");
        response.put("message", "Metrics received successfully");
        
        return Mono.just(ResponseEntity.ok(response));
    }
    

    
    /**
     * Exception handler for general errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        error.put("message", e.getMessage());
        
        return ResponseEntity.internalServerError().body(error);
    }
} 