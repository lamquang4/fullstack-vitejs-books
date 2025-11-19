FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .

COPY uploads ./uploads

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar .
RUN mkdir -p uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]
