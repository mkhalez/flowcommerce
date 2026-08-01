FROM eclipse-temurin:25-jre-alpine

WORKDIR /authentication_service

COPY target/authentication-service-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "app.jar"]

