#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>
#include <unistd.h>
#include "link_sim.h"

// Configuration parameters
double base_latency_ms = 15.0;
double jitter_range_ms = 5.0;
double signal_strength_min_db = -85.0;
double signal_strength_max_db = -45.0;
double packet_loss_max_percent = 2.0;
double bandwidth_min_mbps = 50.0;
double bandwidth_max_mbps = 1000.0;

// Global variables for simulation state
static int initialized = 0;
static unsigned int seed = 0;

void init_link_simulator(void) {
    if (!initialized) {
        seed = (unsigned int)time(NULL);
        srand(seed);
        initialized = 1;
        printf("Link simulator initialized with seed: %u\n", seed);
    }
}

// Generate random double between min and max
static double random_double(double min, double max) {
    return min + ((double)rand() / RAND_MAX) * (max - min);
}

// Generate realistic microwave link metrics
link_metrics_t generate_metrics(void) {
    if (!initialized) {
        init_link_simulator();
    }
    
    link_metrics_t metrics;
    
    // Generate base latency with some variation
    double latency_variation = random_double(-2.0, 2.0);
    metrics.latency_ms = base_latency_ms + latency_variation;
    
    // Generate jitter (variation in latency)
    metrics.jitter_ms = random_double(0.1, jitter_range_ms);
    
    // Generate signal strength (typically between -85 and -45 dBm for microwave)
    metrics.signal_strength_db = random_double(signal_strength_min_db, signal_strength_max_db);
    
    // Generate packet loss rate (typically very low for microwave links)
    metrics.packet_loss_rate = random_double(0.0, packet_loss_max_percent);
    
    // Generate bandwidth (varies based on link quality)
    double bandwidth_factor = (metrics.signal_strength_db - signal_strength_min_db) / 
                            (signal_strength_max_db - signal_strength_min_db);
    bandwidth_factor = fmax(0.1, fmin(1.0, bandwidth_factor)); // Clamp between 0.1 and 1.0
    metrics.bandwidth_mbps = bandwidth_min_mbps + 
                            (bandwidth_max_mbps - bandwidth_min_mbps) * bandwidth_factor;
    
    // Generate SNR (Signal-to-Noise Ratio)
    metrics.snr_db = metrics.signal_strength_db + random_double(10.0, 20.0);
    
    // Set timestamp
    metrics.timestamp = time(NULL);
    
    return metrics;
}

void print_metrics(const link_metrics_t* metrics) {
    printf("=== Microwave Link Metrics ===\n");
    printf("Latency: %.2f ms\n", metrics->latency_ms);
    printf("Jitter: %.2f ms\n", metrics->jitter_ms);
    printf("Signal Strength: %.2f dBm\n", metrics->signal_strength_db);
    printf("Packet Loss Rate: %.3f%%\n", metrics->packet_loss_rate);
    printf("Bandwidth: %.2f Mbps\n", metrics->bandwidth_mbps);
    printf("SNR: %.2f dB\n", metrics->snr_db);
    printf("Timestamp: %ld\n", metrics->timestamp);
    printf("=============================\n");
}

void export_metrics_json(const link_metrics_t* metrics) {
    printf("{\n");
    printf("  \"latency_ms\": %.2f,\n", metrics->latency_ms);
    printf("  \"jitter_ms\": %.2f,\n", metrics->jitter_ms);
    printf("  \"signal_strength_db\": %.2f,\n", metrics->signal_strength_db);
    printf("  \"packet_loss_rate\": %.3f,\n", metrics->packet_loss_rate);
    printf("  \"bandwidth_mbps\": %.2f,\n", metrics->bandwidth_mbps);
    printf("  \"snr_db\": %.2f,\n", metrics->snr_db);
    printf("  \"timestamp\": %ld\n", metrics->timestamp);
    printf("}\n");
}



int main(int argc, char* argv[]) {
    init_link_simulator();
    
    if (argc > 1 && strcmp(argv[1], "--json") == 0) {
        // Output JSON format for API consumption
        link_metrics_t metrics = generate_metrics();
        export_metrics_json(&metrics);
    }
    else {
        // Continuous monitoring mode
        printf("Starting microwave link simulation...\n");
        printf("Press Ctrl+C to stop\n\n");
        
        while (1) {
            link_metrics_t metrics = generate_metrics();
            print_metrics(&metrics);
            sleep(5); // Update every 5 seconds
        }
    }
    
    return 0;
} 