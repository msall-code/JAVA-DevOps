FROM eclipse-temurin:17-jre-alpine

# Sécurité : utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# On cible précisément le JAR du produit
COPY target/product-service-*.jar app.jar

# Correspond à ton server.port=8081
EXPOSE 8081

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]