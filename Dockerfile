# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application, skipping tests to speed up the build
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl \
	&& rm -rf /var/lib/apt/lists/*

# Copy the generated JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the default Eureka Server port
EXPOSE 8761

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=5 \
	CMD curl -fsS http://localhost:8761/actuator/health/readiness || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
