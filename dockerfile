# Stage 1: Build the application using Maven and a JDK image
FROM maven:3.9.3-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# Disable swagger dependency on prod
RUN rm -f src/main/kotlin/com/xu_store/uniform/config/OpenApiConfig.kt || true

RUN mvn package -DskipTests -B

# Stage 2: Minimal secure runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/uniform-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
