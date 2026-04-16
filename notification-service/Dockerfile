# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# On copie d'abord la dépendance commune
COPY common/ /app/common/
RUN cd /app/common && mvn clean install -DskipTests

# On copie et on build le service spécifique
# REMPLACE "order-service" par "product-service" ou "notification-service" pour les autres
COPY order-service/ /app/order-service/
WORKDIR /app/order-service
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# REMPLACE le chemin ici aussi selon le service
COPY --from=build /app/order-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]