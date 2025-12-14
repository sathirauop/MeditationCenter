# Multi-stage Dockerfile for Spring Boot Application
# Stage 1: Build the application using Gradle
# Stage 2: Run the application with a lightweight JRE

# ============================================
# Stage 1: BUILD
# ============================================
FROM eclipse-temurin:21-jdk AS build

# Set working directory inside the container
WORKDIR /app

# Copy Gradle wrapper files first (for better caching)
# Docker caches layers - if these don't change, this layer is reused
COPY gradlew .
COPY gradle gradle/

# Copy build configuration files
COPY build.gradle .
COPY settings.gradle .
COPY lombok.config .

# Download dependencies (this layer is cached unless build.gradle changes)
RUN ./gradlew dependencies --no-daemon || true

# Copy the entire source code (including Flyway migrations)
COPY src src/

# Build the application
# --no-daemon: Don't start Gradle daemon (saves memory in containers)
# -x test: Skip tests for faster builds (run tests separately in CI/CD)
# clean: Ensure fresh build (important for Lombok annotation processing)
# jOOQ code generation happens automatically from Flyway SQL files!
# No database connection needed - reads SQL files directly
RUN ./gradlew clean bootJar --no-daemon -x test

# ============================================
# Stage 2: RUNTIME
# ============================================
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
# The JAR will be in build/libs/ directory
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Create a non-root user for security
# Running as root in containers is a security risk
RUN useradd -m -u 1001 appuser && chown -R appuser:appuser /app
USER appuser

# Run the application
# -Djava.security.egd=file:/dev/./urandom: Faster startup (better random number generation)
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
