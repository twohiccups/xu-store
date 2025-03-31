# Stage 1: Build the application using Maven and Eclipse Temurin OpenJDK 17
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
# Copy the pom.xml and source code into the image
COPY pom.xml .
COPY src/ ./src/
# Build the project and package it, skipping tests for faster builds
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image using Eclipse Temurin OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the generated jar from the builder stage; adjust the jar name if necessary
COPY --from=builder /app/target/uniform-0.0.1-SNAPSHOT.jar app.jar
# Expose the default Spring Boot port
EXPOSE 8080
# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
