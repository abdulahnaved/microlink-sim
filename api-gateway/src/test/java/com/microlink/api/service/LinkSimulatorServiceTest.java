package com.microlink.api.service;

import com.microlink.api.model.LinkMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LinkSimulatorServiceTest {

    @InjectMocks
    private LinkSimulatorService linkSimulatorService;

    @BeforeEach
    void setUp() {
        // Set up test configuration
        ReflectionTestUtils.setField(linkSimulatorService, "linkSimulatorCommand", "test-command");
        ReflectionTestUtils.setField(linkSimulatorService, "timeoutMs", 5000);
    }

    @Test
    void testGetMockMetrics_ValidData() {
        Mono<LinkMetrics> mockMetrics = linkSimulatorService.getMockMetrics();

        StepVerifier.create(mockMetrics)
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
    void testGetMockMetrics_MultipleCalls_DifferentValues() {
        Mono<LinkMetrics> firstCall = linkSimulatorService.getMockMetrics();
        Mono<LinkMetrics> secondCall = linkSimulatorService.getMockMetrics();

        StepVerifier.create(Mono.zip(firstCall, secondCall))
                .assertNext(tuple -> {
                    LinkMetrics first = tuple.getT1();
                    LinkMetrics second = tuple.getT2();
                    
                    // Values should be different due to random generation
                    assertNotEquals(first.getLatencyMs(), second.getLatencyMs());
                    assertNotEquals(first.getJitterMs(), second.getJitterMs());
                    assertNotEquals(first.getSignalStrengthDb(), second.getSignalStrengthDb());
                })
                .verifyComplete();
    }

    @Test
    void testGetCurrentMetrics_ProcessMode() {
        // Test process mode (now the only mode)
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
    void testLinkMetrics_ConstructorAndGetters() {
        long timestamp = Instant.now().getEpochSecond();
        LinkMetrics metrics = new LinkMetrics(15.5, 2.1, -65.2, 0.5, 500.0, -55.0, timestamp);

        assertEquals(15.5, metrics.getLatencyMs());
        assertEquals(2.1, metrics.getJitterMs());
        assertEquals(-65.2, metrics.getSignalStrengthDb());
        assertEquals(0.5, metrics.getPacketLossRate());
        assertEquals(500.0, metrics.getBandwidthMbps());
        assertEquals(-55.0, metrics.getSnrDb());
        assertEquals(timestamp, metrics.getTimestamp());
    }

    @Test
    void testLinkMetrics_ToString() {
        LinkMetrics metrics = new LinkMetrics(15.5, 2.1, -65.2, 0.5, 500.0, -55.0, 1754258000L);
        String result = metrics.toString();

        assertTrue(result.contains("latencyMs=15.5"));
        assertTrue(result.contains("jitterMs=2.1"));
        assertTrue(result.contains("signalStrengthDb=-65.2"));
        assertTrue(result.contains("packetLossRate=0.5"));
        assertTrue(result.contains("bandwidthMbps=500.0"));
        assertTrue(result.contains("snrDb=-55.0"));
        assertTrue(result.contains("timestamp=1754258000"));
    }

    @Test
    void testLinkMetrics_DefaultConstructor() {
        LinkMetrics metrics = new LinkMetrics();
        
        assertNotNull(metrics);
        assertNull(metrics.getLatencyMs());
        assertNull(metrics.getJitterMs());
        assertNull(metrics.getSignalStrengthDb());
        assertNull(metrics.getPacketLossRate());
        assertNull(metrics.getBandwidthMbps());
        assertNull(metrics.getSnrDb());
        assertNull(metrics.getTimestamp());
    }

    @Test
    void testLinkMetrics_Setters() {
        LinkMetrics metrics = new LinkMetrics();
        
        metrics.setLatencyMs(16.5);
        metrics.setJitterMs(3.2);
        metrics.setSignalStrengthDb(-70.0);
        metrics.setPacketLossRate(1.5);
        metrics.setBandwidthMbps(600.0);
        metrics.setSnrDb(-50.0);
        metrics.setTimestamp(1754258000L);

        assertEquals(16.5, metrics.getLatencyMs());
        assertEquals(3.2, metrics.getJitterMs());
        assertEquals(-70.0, metrics.getSignalStrengthDb());
        assertEquals(1.5, metrics.getPacketLossRate());
        assertEquals(600.0, metrics.getBandwidthMbps());
        assertEquals(-50.0, metrics.getSnrDb());
        assertEquals(1754258000L, metrics.getTimestamp());
    }
} 