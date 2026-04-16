# Stage 1: Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# On copie le pom.xml d'abord pour optimiser le cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline
# On copie les sources et on build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Image finale légère
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# AJUSTE LE PORT ICI (8081, 8082 ou 8083)
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]