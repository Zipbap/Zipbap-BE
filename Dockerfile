FROM gradle:8.7-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :api-user:bootJar --no-daemon

FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/api-user/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
