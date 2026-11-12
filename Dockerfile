FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests && \
    cp $(ls /app/target/*.jar | grep -v "original-" | head -n 1) /app/app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/app.jar app.jar

ENTRYPOINT ["java", "-cp", "app.jar", "Main"]
