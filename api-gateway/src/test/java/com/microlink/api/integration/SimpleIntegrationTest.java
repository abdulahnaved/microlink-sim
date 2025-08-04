package com.microlink.api.integration;

import com.microlink.api.model.LinkMetrics;
import com.microlink.api.service.LinkSimulatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "link.simulator.mode=process",
    "link.simulator.command=../link-sim/link_sim.exe",
    "server.port=0"
})
class SimpleIntegrationTest {

    @Autowired
    private LinkSimulatorService linkSimulatorService;

    @Test
    void testLinkSimulatorService_GetCurrentMetrics() {
        Mono<LinkMetrics> result = linkSimulatorService.getCurrentMetrics();

        StepVerifier.create(result)
                .assertNext(metrics -> {
                    assertNotNull(metrics);
                    assertTrue(metrics.getLatencyMs() > 0);
                    assertTrue(metrics.getJitterMs() >= 0);
                    assertTrue(metrics.getSignalStrengthDb() < 0);
                    assertTrue(metrics.getPacketLossRate() >= 0);
                    assertTrue(metrics.getBandwidthMbps() > 0);
                    assertTrue(metrics.getSnrDb() < 0);
                    assertTrue(metrics.getTimestamp() > 0);
                })
                .verifyComplete();
    }

    @Test
    void testLinkSimulatorService_GetMockMetrics() {
        Mono<LinkMetrics> result = linkSimulatorService.getMockMetrics();

        StepVerifier.create(result)
                .assertNext(metrics -> {
                    assertNotNull(metrics);
                    assertTrue(metrics.getLatencyMs() >= 15.0 && metrics.getLatencyMs() <= 19.0);
                    assertTrue(metrics.getJitterMs() >= 0.1 && metrics.getJitterMs() <= 5.0);
                    assertTrue(metrics.getSignalStrengthDb() >= -85.0 && metrics.getSignalStrengthDb() <= -45.0);
                    assertTrue(metrics.getPacketLossRate() >= 0.0 && metrics.getPacketLossRate() <= 2.0);
                    assertTrue(metrics.getBandwidthMbps() >= 50.0 && metrics.getBandwidthMbps() <= 1000.0);
                    assertTrue(metrics.getSnrDb() >= -80.0 && metrics.getSnrDb() <= -30.0);
                    assertTrue(metrics.getTimestamp() > 0);
                })
                .verifyComplete();
    }

    @Test
    void testLinkMetrics_ModelValidation() {
        LinkMetrics metrics = new LinkMetrics(16.5, 2.5, -70.0, 1.0, 600.0, -55.0, 1754258000L);

        assertEquals(16.5, metrics.getLatencyMs());
        assertEquals(2.5, metrics.getJitterMs());
        assertEquals(-70.0, metrics.getSignalStrengthDb());
        assertEquals(1.0, metrics.getPacketLossRate());
        assertEquals(600.0, metrics.getBandwidthMbps());
        assertEquals(-55.0, metrics.getSnrDb());
        assertEquals(1754258000L, metrics.getTimestamp());

        // Test toString method
        String toString = metrics.toString();
        assertTrue(toString.contains("latencyMs=16.5"));
        assertTrue(toString.contains("jitterMs=2.5"));
        assertTrue(toString.contains("signalStrengthDb=-70.0"));
        assertTrue(toString.contains("packetLossRate=1.0"));
        assertTrue(toString.contains("bandwidthMbps=600.0"));
        assertTrue(toString.contains("snrDb=-55.0"));
        assertTrue(toString.contains("timestamp=1754258000"));
    }

    @Test
    void testLinkMetrics_RealisticValues() {
        // Test with realistic microwave link values
        LinkMetrics metrics = new LinkMetrics(18.2, 3.1, -68.5, 0.8, 750.0, -52.3, 1754258000L);

        // Validate realistic ranges
        assertTrue(metrics.getLatencyMs() >= 15.0 && metrics.getLatencyMs() <= 20.0);
        assertTrue(metrics.getJitterMs() >= 0.1 && metrics.getJitterMs() <= 5.0);
        assertTrue(metrics.getSignalStrengthDb() >= -85.0 && metrics.getSignalStrengthDb() <= -45.0);
        assertTrue(metrics.getPacketLossRate() >= 0.0 && metrics.getPacketLossRate() <= 2.0);
        assertTrue(metrics.getBandwidthMbps() >= 50.0 && metrics.getBandwidthMbps() <= 1000.0);
        assertTrue(metrics.getSnrDb() >= -80.0 && metrics.getSnrDb() <= -30.0);
        assertTrue(metrics.getTimestamp() > 0);
    }
} 