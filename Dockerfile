# ---- Build Stage ----
FROM gradle:8.7-jdk17 AS builder

WORKDIR /app

# Copy gradle config files first for dependency caching
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Download dependencies (layer cached unless build files change)
RUN gradle dependencies --no-daemon || true

# Copy source code and build the JAR
COPY src src
RUN gradle bootJar --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
