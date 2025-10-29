# --- 빌드 단계 ---
FROM gradle:8.7-jdk17 AS builder
WORKDIR /app
COPY . .

# SERVICE 환경변수(api-user or api-admin)에 따라 빌드 타깃 분기
ARG SERVICE=api-user
RUN ./gradlew :${SERVICE}:bootJar --no-daemon

# --- 런타임 단계 ---
FROM eclipse-temurin:17-jdk
WORKDIR /app

# 서비스별 jar 복사
ARG SERVICE=api-user
COPY --from=builder /app/${SERVICE}/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
