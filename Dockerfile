# Gunakan Java 21 (LTS terbaru)
FROM maven:latest AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage dengan Dockerize
FROM openjdk:17-jdk-slim

# Install dockerize
ENV DOCKERIZE_VERSION v0.6.1
RUN apt-get update && apt-get install -y wget \
    && wget -O /tmp/dockerize.tar.gz https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf /tmp/dockerize.tar.gz \
    && rm /tmp/dockerize.tar.gz \
    && apt-get remove -y wget && apt-get autoremove -y

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9014

# Run sebagai non-root user untuk security
RUN addgroup --system --gid 1000 appuser && \
    adduser --system --uid 1000 --gid 1000 appuser
USER appuser

# Gunakan dockerize untuk menunggu semua dependencies siap
CMD dockerize \
    -wait tcp://mysql-new:3306 \
    -wait tcp://redis-new:6379 \
    -wait tcp://rabbitmq-new:5672 \
    -timeout 120s \
    java -jar app.jar