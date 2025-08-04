#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
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

// Simple HTTP response function
void send_http_response(int client_socket, int status_code, const char* content_type, const char* body) {
    char response[4096];
    snprintf(response, sizeof(response),
        "HTTP/1.1 %d OK\r\n"
        "Content-Type: %s\r\n"
        "Content-Length: %zu\r\n"
        "Access-Control-Allow-Origin: *\r\n"
        "Connection: close\r\n"
        "\r\n"
        "%s",
        status_code, content_type, strlen(body), body);
    
    send(client_socket, response, strlen(response), 0);
}

// Handle HTTP requests
void handle_http_request(int client_socket, const char* request) {
    if (strstr(request, "GET /metrics") != NULL) {
        link_metrics_t metrics = generate_metrics();
        
        char json_response[512];
        snprintf(json_response, sizeof(json_response),
            "{\n"
            "  \"latency_ms\": %.2f,\n"
            "  \"jitter_ms\": %.2f,\n"
            "  \"signal_strength_db\": %.2f,\n"
            "  \"packet_loss_rate\": %.3f,\n"
            "  \"bandwidth_mbps\": %.2f,\n"
            "  \"snr_db\": %.2f,\n"
            "  \"timestamp\": %ld\n"
            "}",
            metrics.latency_ms, metrics.jitter_ms, metrics.signal_strength_db,
            metrics.packet_loss_rate, metrics.bandwidth_mbps, metrics.snr_db,
            metrics.timestamp);
        
        send_http_response(client_socket, 200, "application/json", json_response);
    }
    else if (strstr(request, "GET /health") != NULL) {
        send_http_response(client_socket, 200, "text/plain", "OK");
    }
    else {
        send_http_response(client_socket, 404, "text/plain", "Not Found");
    }
}

// Start HTTP server
void start_http_server(int port) {
    int server_socket, client_socket;
    struct sockaddr_in server_addr, client_addr;
    socklen_t client_len = sizeof(client_addr);
    char buffer[1024];
    
    // Create socket
    server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket < 0) {
        perror("Socket creation failed");
        exit(1);
    }
    
    // Set socket options
    int opt = 1;
    setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
    
    // Bind socket
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(port);
    
    if (bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("Bind failed");
        exit(1);
    }
    
    // Listen for connections
    if (listen(server_socket, 5) < 0) {
        perror("Listen failed");
        exit(1);
    }
    
    printf("HTTP server started on port %d\n", port);
    printf("Available endpoints:\n");
    printf("  GET /metrics - Get current link metrics\n");
    printf("  GET /health  - Health check\n");
    
    while (1) {
        client_socket = accept(server_socket, (struct sockaddr*)&client_addr, &client_len);
        if (client_socket < 0) {
            perror("Accept failed");
            continue;
        }
        
        // Read request
        int bytes_read = recv(client_socket, buffer, sizeof(buffer) - 1, 0);
        if (bytes_read > 0) {
            buffer[bytes_read] = '\0';
            handle_http_request(client_socket, buffer);
        }
        
        close(client_socket);
    }
    
    close(server_socket);
}

int main(int argc, char* argv[]) {
    init_link_simulator();
    
    if (argc > 1 && strcmp(argv[1], "--json") == 0) {
        // Output JSON format for API consumption
        link_metrics_t metrics = generate_metrics();
        export_metrics_json(&metrics);
    }
    else if (argc > 1 && strcmp(argv[1], "--http") == 0) {
        // Start HTTP server mode
        int port = 8080;
        if (argc > 2) {
            port = atoi(argv[2]);
        }
        start_http_server(port);
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