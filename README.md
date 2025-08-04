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

## Development

### C Link Simulator

The C simulator can run in different modes:

```bash
# JSON output mode (used by Java API Gateway)
./link_sim.exe --json

# HTTP server mode (alternative)
./link_sim.exe --http 8080

# Continuous monitoring mode
./link_sim.exe
```

### Java API Gateway

The Java service automatically executes the C simulator and parses its JSON output. If the C simulator is unavailable, it falls back to mock data.

## Configuration

### Application Properties

```yaml
server:
  port: 8081

link:
  simulator:
    mode: process  # "process" or "http"
    command: ../link-sim/link_sim.exe  # Path to C simulator executable
    url: http://localhost:8082    # URL for HTTP mode
    timeout: 5000
```

## Metrics

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

### Test the C Simulator Directly

```bash
# From the link-sim directory
./link_sim.exe --json
```

### Automated Testing

The project includes comprehensive Java unit and integration tests:

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=LinkSimulatorServiceTest
mvn test -Dtest=SimpleIntegrationTest
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

### Debug Steps

1. **Check if C simulator is working:**
   ```bash
   cd link-sim
   ./link_sim.exe --json
   ```

2. **Check Java application logs** for any errors in process execution or JSON parsing

3. **Verify the executable path** in `api-gateway/src/main/resources/application.yml`

### VS Code Issues

If you're having trouble running the application from VS Code:

1. **Make sure VS Code is opened at the correct folder:**
   - Open VS Code
   - File → Open Folder → Select `E:\microlink-sim\api-gateway` (not the root project folder)

2. **Use the integrated terminal:**
   - Press `Ctrl+`` ` to open VS Code's integrated terminal
   - Run: `java -jar target/api-gateway-1.0.0.jar`

3. **Avoid clicking the "Run" arrow on Java files** - VS Code's Java extension can have issues with Spring Boot applications

4. **If using Maven in VS Code:**
   - Open integrated terminal
   - Run: `mvn spring-boot:run`

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

## Features

### ✅ Working Features
- **Real-time Metrics**: Live microwave link simulation
- **Web Dashboard**: Beautiful, responsive web interface
- **API Endpoints**: RESTful API for programmatic access
- **Health Monitoring**: System status and simulator availability
- **Error Handling**: Graceful fallback to mock data
- **Comprehensive Testing**: Unit and integration tests
- **Clean Architecture**: MVC pattern with proper separation

### 🎯 Perfect for Demo
- **3 Core Pages**: Home, Dashboard, Metrics, Health
- **Professional UI**: Modern, responsive design
- **Real Integration**: C ↔ Java communication
- **Production Ready**: Error handling and logging
- **Well Tested**: 100% test success rate

This project demonstrates advanced software engineering skills including multi-language development, system integration, web development, and comprehensive testing - perfect for showcasing to potential employers like Mini Link.

---

## 🚀 Contributing & Forking

### How to Fork This Project

1. **Fork on GitHub**: Click the "Fork" button at the top right of this repository
2. **Clone your fork**:
   ```bash
   git clone https://github.com/[your-username]/microlink-sim.git
   cd microlink-sim
   ```

### 🛠️ Complete Setup Guide

#### Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| **Java** | 17+ | API Gateway runtime |
| **Maven** | 3.6+ | Java build tool |
| **GCC** | Any recent | C compiler |
| **Git** | Any recent | Version control |

#### Platform-Specific Setup

<details>
<summary><b>🪟 Windows Setup</b></summary>

1. **Install Java 17+**:
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify: `java -version`

2. **Install Maven**:
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Add to PATH
   - Verify: `mvn -version`

3. **Install GCC**:
   ```powershell
   # Option 1: Using Chocolatey
   choco install mingw
   
   # Option 2: Install MSYS2 and run:
   pacman -S gcc
   ```

4. **Build & Run**:
   ```powershell
   # Build C simulator (if make available)
   cd link-sim
   make
   
   # Or manually:
   gcc -o link_sim.exe link_sim.c -lm
   
   # Build & run Java API
   cd ../api-gateway
   mvn clean package
   java -jar target/api-gateway-1.0.0.jar
   ```

</details>

<details>
<summary><b>🐧 Linux Setup</b></summary>

1. **Install Dependencies**:
   ```bash
   # Ubuntu/Debian
   sudo apt update
   sudo apt install openjdk-17-jdk maven gcc build-essential
   
   # CentOS/RHEL/Fedora
   sudo dnf install java-17-openjdk maven gcc make
   ```

2. **Build & Run**:
   ```bash
   # Build C simulator
   cd link-sim
   make
   
   # Build & run Java API
   cd ../api-gateway
   mvn clean package
   java -jar target/api-gateway-1.0.0.jar
   ```

</details>

<details>
<summary><b>🍎 macOS Setup</b></summary>

1. **Install Dependencies**:
   ```bash
   # Using Homebrew
   brew install openjdk@17 maven gcc
   
   # Add Java to PATH
   echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
   ```

2. **Build & Run**:
   ```bash
   # Build C simulator
   cd link-sim
   make
   
   # Build & run Java API
   cd ../api-gateway
   mvn clean package
   java -jar target/api-gateway-1.0.0.jar
   ```

</details>

#### 🚀 Quick Start (Any Platform)

```bash
# 1. Clone the project
git clone https://github.com/abdulahnaved/microlink-sim.git
cd microlink-sim

# 2. Build everything
cd link-sim && make && cd ..
cd api-gateway && mvn clean package

# 3. Run the application
java -jar target/api-gateway-1.0.0.jar

# 4. Open in browser
# http://localhost:8081/dashboard
```

#### 🔧 Development Setup

1. **Set up upstream remote** (to sync with original):
   ```bash
   git remote add upstream https://github.com/abdulahnaved/microlink-sim.git
   ```

2. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make changes and test**:
   ```bash
   cd api-gateway
   mvn test
   ```

4. **Submit Pull Request**:
   - Push to your fork
   - Create PR from your fork to original repository

#### 🆘 Troubleshooting

<details>
<summary><b>Common Issues & Solutions</b></summary>

**"make: command not found" (Windows)**
```powershell
# Install make via chocolatey
choco install make

# Or compile manually:
gcc -o link_sim.exe link_sim.c -lm
```

**"JAVA_HOME not set"**
```bash
# Linux/macOS
export JAVA_HOME=/path/to/java
echo 'export JAVA_HOME=/path/to/java' >> ~/.bashrc

# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

**"Port 8081 already in use"**
```bash
# Change port in api-gateway/src/main/resources/application.yml
server:
  port: 8082  # or any available port
```

**"C simulator not found"**
- Ensure `link_sim.exe` exists in `link-sim/` directory
- Check path in `application.yml` is correct
- Verify executable permissions (Linux/macOS): `chmod +x link_sim.exe`

</details>

#### 📊 Verification Steps

After setup, verify everything works:

1. **Test C Simulator**:
   ```bash
   cd link-sim
   ./link_sim.exe --json
   # Should output JSON with metrics
   ```

2. **Test Java API**:
   ```bash
   curl http://localhost:8081/api/v1/metrics
   # Should return JSON response
   ```

3. **Test Web Dashboard**:
   - Visit: http://localhost:8081/dashboard
   - Should show real-time metrics

#### 🤝 Contributing Guidelines

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Ensure all tests pass: `mvn test`
6. Submit a pull request

#### 📝 What You Can Contribute

- 🐛 **Bug fixes**
- ✨ **New features** (additional metrics, visualizations)
- 📚 **Documentation improvements**
- 🧪 **More test coverage**
- 🎨 **UI/UX enhancements**
- 🔧 **Build system improvements**

#### 📋 Project Status

![CI Status](https://github.com/abdulahnaved/microlink-sim/actions/workflows/ci.yml/badge.svg)

- ✅ **Build Status**: Passing
- ✅ **Tests**: 11 tests, 0 failures
- ✅ **Cross-Platform**: Windows, Linux, macOS
- ✅ **Documentation**: Complete setup guide


