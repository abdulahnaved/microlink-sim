#ifndef LINK_SIM_H
#define LINK_SIM_H

#include <stdint.h>
#include <time.h>

// Microwave radio link metrics structure
typedef struct {
    double latency_ms;        // Round-trip latency in milliseconds
    double jitter_ms;         // Jitter (variation in latency) in milliseconds
    double signal_strength_db; // Signal strength in dBm
    double packet_loss_rate;   // Packet loss rate as percentage
    double bandwidth_mbps;     // Available bandwidth in Mbps
    double snr_db;            // Signal-to-Noise Ratio in dB
    time_t timestamp;         // Unix timestamp
} link_metrics_t;

// Function declarations
void init_link_simulator(void);
link_metrics_t generate_metrics(void);
void print_metrics(const link_metrics_t* metrics);
void export_metrics_json(const link_metrics_t* metrics);



// Configuration parameters
extern double base_latency_ms;
extern double jitter_range_ms;
extern double signal_strength_min_db;
extern double signal_strength_max_db;
extern double packet_loss_max_percent;
extern double bandwidth_min_mbps;
extern double bandwidth_max_mbps;

#endif // LINK_SIM_H 