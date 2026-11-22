#                                 СБОРКА ОБРАЗА
#БЕРЕМ ЗА ОСНОВУ ГОТОВЫЙ ОБРАЗ MAVEN
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
#ЗАДАЕМ РАБОЧУЮ ПАПКУ ВНУТРИ КОНТЕЙНЕРА
WORKDIR /app
# КОПИРУЕМ POM.XML И САМ ПРОЕКТ
COPY pom.xml .
COPY src ./src
#собирает jar
RUN mvn -B package
#                               ЗАПУСК
# начинаем с чистого листа новый образ где есть jre
FROM eclipse-temurin:21-jre-alpine
# рабочая папка в новом образе
WORKDIR /app
#берет файл со сборки и берет только файл .jar и называет его app.jar
COPY --from=builder /app/target/app.jar app.jar
#Задаем что будет запускаться "запуски main из app.jar
ENTRYPOINT ["java", "-cp", "app.jar", "Main"]
