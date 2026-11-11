# ЭТАП 1: Сборка проекта
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Сначала копируем только pom.xml для кэширования библиотек
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходный код и собираем JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ЭТАП 2: Запуск готового приложения
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный JAR из первого этапа
COPY --from=builder /app/target/*-shaded.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]