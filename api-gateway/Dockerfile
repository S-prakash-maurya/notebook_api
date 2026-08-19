FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY build/libs/api-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8787

ENTRYPOINT ["java", "-jar", "app.jar"]