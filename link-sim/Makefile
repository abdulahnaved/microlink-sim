# Makefile for Microlink C Link Simulator

CC = gcc
CFLAGS = -Wall -Wextra -std=c99 -O2
TARGET = link_sim
SOURCES = link_sim.c
HEADERS = link_sim.h

# Default target
all: $(TARGET)

# Build the executable
$(TARGET): $(SOURCES) $(HEADERS)
	$(CC) $(CFLAGS) -o $(TARGET) $(SOURCES) -lm

# Clean build artifacts
clean:
	rm -f $(TARGET) $(TARGET).exe *.o

# Install dependencies (for development)
install-deps:
	# No external dependencies required for basic build
	@echo "No external dependencies required"

# Test the build
test: $(TARGET)
	@echo "Testing link simulator..."
	./$(TARGET) --json

# Help target
help:
	@echo "Available targets:"
	@echo "  all        - Build the link simulator (default)"
	@echo "  clean      - Remove build artifacts"
	@echo "  test       - Test the built executable"
	@echo "  help       - Show this help message"

.PHONY: all clean test help install-deps 