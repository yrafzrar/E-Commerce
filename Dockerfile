FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY GamesRun/ .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar ./app.jar

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8081} -jar app.jar"]