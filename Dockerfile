#FROM eclipse-temurin:11-jdk-alpine
FROM --platform=linux/x86_64 eclipse-temurin:11-jdk
#FROM arm64v8/eclipse-temurin:11-jdk

EXPOSE 8080

RUN mkdir -p /opt/ucb-judge/logs/uj-users
VOLUME /opt/ucb-judge/logs/uj-users

VOLUME /tmp

# Server
ENV PORT="PORT"

# Database
ENV POSTGRES_USERNAME="POSTGRES_USERNAME"
ENV POSTGRES_PASSWORD="POSTGRES_PASSWORD"
ENV POSTGRES_URL="POSTGRES_URL"

# Config server
ENV CONFIG_SERVER_URI="CONFIG_SERVER_URI"
ENV CONFIG_SERVER_PROFILE="CONFIG_SERVER_PROFILE"

# Tracing
ENV EUREKA_SERVER_URI="EUREKA_SERVER_URI"
ENV ZIPKIN_SERVER_URI="ZIPKIN_SERVER_URI"

# Keycloak
ENV KEYCLOAK_SERVER_URI="KEYCLOAK_SERVER_URI"
ENV KEYCLOAK_CLIENT_SECRET="KEYCLOAK_CLIENT_SECRET"
ENV KEYCLOAK_REALM="KEYCLOAK_REALM"
ENV KEYCLOAK_CLIENT_ID="KEYCLOAK_CLIENT_ID"
ENV FRONTEND_KEYCLOAK_CLIENT_ID="FRONTEND_KEYCLOAK_CLIENT_ID"

ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","ucb.judge.ujusers.UjUsersApplicationKt"]