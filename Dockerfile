# Build stage
FROM maven:3.9.6-openjdk-21-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:21-jre-slim
ENV TZ=Asia/Riyadh

WORKDIR /app
COPY --from=build /app/target/discount-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
