# ---- Build Stage ----
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy wrapper scripts and wrapper/ dir first
COPY gradlew gradlew.bat ./
COPY gradle gradle

# Set execute bit (on-disk permissions are -rw-r--r--)
RUN chmod +x gradlew

# Copy build descriptors for dependency caching
COPY build.gradle settings.gradle ./

# Pre-download dependencies (cached layer if build files unchanged)
RUN ./gradlew dependencies --no-daemon || true

# Copy source and build the JAR
COPY src src
RUN ./gradlew bootJar --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
