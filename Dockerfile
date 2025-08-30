# Gunakan versi specific untuk reproducibility
FROM maven:3.8.6-openjdk-17 AS build

WORKDIR /app

# Copy pom.xml pertama untuk layer caching
COPY pom.xml .
# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
# Build application
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim

# Install dockerize
ENV DOCKERIZE_VERSION v0.6.1
RUN apt-get update && \
    apt-get install -y wget && \
    wget -O /tmp/dockerize.tar.gz https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && \
    tar -C /usr/local/bin -xzvf /tmp/dockerize.tar.gz && \
    rm /tmp/dockerize.tar.gz && \
    apt-get remove -y wget && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create non-root user
RUN groupadd -r appuser -g 1000 && \
    useradd -r -u 1000 -g appuser -s /sbin/nologin appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 9014

# Gunakan dockerize untuk wait dependencies
CMD dockerize \
    -wait tcp://mysql-new:3306 \
    -wait tcp://redis-new:6379 \
    -wait tcp://rabbitmq-new:5672 \
    -timeout 120s \
    java -jar app.jar