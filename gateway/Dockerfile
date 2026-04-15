FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/gateway.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]