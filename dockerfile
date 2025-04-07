# Stage 1: Build the application using Maven and a JDK image
FROM maven:3.9.3-eclipse-temurin-17-alpine AS build
WORKDIR /app
# Copy the pom and source code; leveraging Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Create a minimal runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the jar from the build stage; adjust the jar name as needed
COPY --from=build /app/target/uniform-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]