# Build stage
FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=build /app/backend/target/backend-0.0.1-SNAPSHOT.jar .

COPY --from=build /app/uploads ./uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]
