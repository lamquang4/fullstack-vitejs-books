FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY backend/pom.xml .
COPY backend/mvnw .
COPY backend/.mvn ./.mvn
COPY backend/src ./src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]
