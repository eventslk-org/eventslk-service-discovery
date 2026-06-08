# syntax=docker/dockerfile:1.7
###############################################################################
# EventsLK :: ServiceDiscovery (Eureka Server) — Java 21 / Spring Boot 3.2.4
# Multi-stage, digest-pinned, non-root, healthchecked.
###############################################################################

# ---------- Stage 1: Build ----------
# maven:3.9-eclipse-temurin-21 (digest resolved 2026-06-08). Pinned by digest so
# the build is reproducible and immune to tag re-pointing / supply-chain swaps.
FROM maven@sha256:d7e7f57407437c014571f1ad5a9955f03fc3edcb1d964067ef351fa38e798665 AS build

WORKDIR /workspace

# Copy only the POM first so dependency resolution becomes its own cached layer.
# Re-used on every build where pom.xml is unchanged -> fast incremental builds.
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp dependency:go-offline

# Now copy sources and build. Unit tests run in the CI pipeline, not here.
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp clean package -DskipTests

# ---------- Stage 2: Runtime ----------
# eclipse-temurin:21-jre-jammy (digest resolved 2026-06-08), pinned by digest.
FROM eclipse-temurin@sha256:199aebeb3adcde4910695cdebfe782ada38dadb6cc8013159b58d3724451befd AS runtime

# curl is required for the container HEALTHCHECK. Create a fixed-UID non-root
# account; purge apt lists afterwards to keep the layer small.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 10001 spring \
    && useradd  --system --uid 10001 --gid spring --home-dir /app --no-create-home spring

WORKDIR /app

# Copy only the fat JAR (*.jar skips the *.jar.original sidecar artefact).
COPY --from=build --chown=spring:spring /workspace/target/*.jar app.jar

# Drop privileges. Numeric form satisfies Kubernetes runAsNonRoot.
USER 10001:10001

EXPOSE 8761

# In-container health probe. start-period covers JVM + Eureka warm-up.
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD curl -fsS http://127.0.0.1:8761/actuator/health || exit 1

# MaxRAMPercentage makes the JVM respect the container's cgroup memory limit.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
