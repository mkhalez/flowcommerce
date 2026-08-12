FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml ./pom.xml
COPY authentication-service/pom.xml ./authentication-service/pom.xml

WORKDIR /build/authentication-service
RUN mvn dependency:go-offline -B

COPY authentication-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /authentication-service

COPY --from=builder /build/authentication-service/target/authentication-servicee-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker
CMD ["java", "-jar", "app.jar"]

