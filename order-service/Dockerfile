FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml ./pom.xml
COPY order-service/pom.xml ./order-service/pom.xml

WORKDIR /build/order-service
RUN mvn dependency:go-offline -B

COPY order-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /order_service

COPY --from=builder /build/order-service/target/order-service-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker
CMD ["java", "-jar", "app.jar"]

