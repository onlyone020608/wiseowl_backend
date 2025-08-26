# ---- build stage ----
FROM gradle:8.8-jdk21 AS build
WORKDIR /home/gradle/project

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon || true

COPY . .

# JAR 빌드
RUN ./gradlew bootJar -x test --no-daemon

# ---- run stage ----
FROM eclipse-temurin:21-jre-alpine
ENV TZ=Asia/Seoul
WORKDIR /app

# 빌드 결과 JAR 복사
COPY --from=build /home/gradle/project/build/libs/*SNAPSHOT*.jar app.jar

# 비루트 권한 실행
USER 1000

# 메모리 안전 옵션 포함
ENTRYPOINT ["java","-XX:+ExitOnOutOfMemoryError","-Xms256m","-Xmx512m","-jar","/app/app.jar"]
