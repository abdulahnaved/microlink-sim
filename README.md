# Microlink Simulator

A comprehensive microwave link simulation system with C-based link simulator and Java API Gateway.

## Architecture

The system consists of two main components:

1. **C Link Simulator** (`link-sim/`) - Generates realistic microwave link metrics
2. **Java API Gateway** (`api-gateway/`) - Provides REST API and web dashboard

## Communication Flow

The Java API Gateway communicates with the C Link Simulator via direct process execution:

```
Java API Gateway ←→ Process Execution ←→ C Link Simulator
```

The Java service executes the C simulator binary and parses its JSON output to provide real-time metrics.

## Quick Start

### Prerequisites

- Java 17+ 
- GCC (for C compilation)
- Maven (for Java build)

### Building the C Simulator

1. **Navigate to the link-sim directory:**
   ```bash
   cd link-sim
   ```

2. **Compile the C simulator:**
   ```bash
   make
   ```
   or manually:
   ```bash
   gcc -o link_sim.exe link_sim.c -lm
   ```

### Running the Java API Gateway

1. **Navigate to the api-gateway directory:**
   ```bash
   cd api-gateway
   ```

2. **Build the Java application:**
   ```bash
   mvn clean package
   ```

3. **Run the application (recommended method):**
   ```bash
   java -jar target/api-gateway-1.0.0.jar
   ```

   **Alternative methods:**
   - **Using Maven:** `mvn spring-boot:run`
   - **Using VS Code:** Open the `api-gateway` folder in VS Code, then use the integrated terminal to run `java -jar target/api-gateway-1.0.0.jar`

### Access Points

- **Home Page**: http://localhost:8081/ - Welcome page with API documentation
- **Dashboard**: http://localhost:8081/dashboard - Real-time metrics visualization
- **Metrics Page**: http://localhost:8081/metrics-page - Detailed metrics display
- **Health Page**: http://localhost:8081/health-page - System health status

### API Endpoints

- **Current Metrics**: http://localhost:8081/api/v1/metrics
- **Health Check**: http://localhost:8081/api/v1/metrics/health
- **Submit Metrics**: http://localhost:8081/api/v1/metrics (POST)

## Web Interface

The system provides a clean, modern web interface with three main pages:

### 🏠 Home Page
- Welcome page with project overview
- API endpoint documentation
- Navigation to all features

### 📊 Dashboard
- Real-time microwave link metrics
- Beautiful card-based visualization
- Auto-refresh every 5 seconds
- Performance overview section

### 📈 Metrics Page
- Detailed metrics display
- All microwave link parameters
- Timestamp information
- Auto-refresh every 10 seconds

### ❤️ Health Page
- System health status
- Simulator availability
- Operational information
- Auto-refresh every 10 seconds




The system generates realistic microwave link metrics:

- **Latency**: 15-19ms (base + variation)
- **Jitter**: 0.1-5ms (latency variation)
- **Signal Strength**: -85 to -45 dBm
- **Packet Loss**: 0-2%
- **Bandwidth**: 50-1000 Mbps (based on signal quality)
- **SNR**: Signal-to-Noise Ratio in dB

## Testing

### Manual Testing

```bash
# Test current metrics
curl http://localhost:8081/api/v1/metrics

# Test health check
curl http://localhost:8081/api/v1/metrics/health

# Test web dashboard
curl http://localhost:8081/dashboard
```

#### Test Coverage

- **Service Tests**: Test the `LinkSimulatorService` functionality
  - Mock data generation
  - Process and HTTP mode switching
  - Error handling and fallback mechanisms
  - Data validation and boundary testing

- **Integration Tests**: Test the complete system integration
  - C simulator communication
  - Real metrics data validation
  - Model validation and serialization

- **Model Tests**: Test the `LinkMetrics` data model
  - Constructor and getter/setter methods
  - JSON serialization/deserialization
  - Realistic value validation

#### Test Results

The tests validate:
- ✅ **Service Layer**: Mock data generation and error handling
- ✅ **Integration**: Real C simulator communication
- ✅ **Data Model**: LinkMetrics validation and serialization
- ✅ **Boundary Values**: Realistic microwave link metrics ranges
- ✅ **Error Scenarios**: Fallback to mock data when C simulator fails

## Troubleshooting

### Common Issues

- **C simulator not found**: Ensure the C simulator is compiled and the path in `application.yml` is correct
- **Port conflicts**: The application runs on port 8081 by default
- **JSON parsing errors**: The Java service extracts JSON from C simulator output that may contain initialization messages


## Project Structure

```
microlink-sim/
├── api-gateway/          # Java API Gateway (Spring Boot)
│   ├── src/main/java/   # Java source code
│   │   └── com/microlink/api/
│   │       ├── controller/  # REST API controllers
│   │       ├── service/     # Business logic
│   │       └── model/       # Data models
│   ├── src/main/resources/ # Configuration and templates
│   │   └── templates/       # HTML templates
│   ├── src/test/java/      # Test classes
│   └── target/          # Compiled artifacts
├── link-sim/            # C Link Simulator
│   ├── link_sim.c      # C source code
│   ├── link_sim.h      # Header file
│   ├── link_sim.exe    # Compiled executable
│   └── Makefile        # Build configuration
└── README.md           # This file
```


#### 📋 Project Status

![CI Status](https://github.com/abdulahnaved/microlink-sim/actions/workflows/ci.yml/badge.svg)

- ✅ **Build Status**: Passing
- ✅ **Tests**: 11 tests, 0 failures
- ✅ **Cross-Platform**: Windows, Linux, macOS
- ✅ **Documentation**: Complete setup guide


