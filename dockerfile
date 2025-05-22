# Stage 1: Build the application using Maven and a JDK image
FROM maven:3.9.3-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B -Dspring.profiles.active=prod -P!swagger

# Stage 2: Minimal secure runtime image
FROM gcr.io/distroless/java17-debian11
WORKDIR /app

COPY --from=build /app/target/uniform-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
