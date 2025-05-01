FROM gradle:7.6.1-jdk17 as build

WORKDIR /app
COPY . .
RUN gradle --no-daemon build

FROM openjdk:17-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Health check for Kubernetes
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 CMD curl -f http://localhost:8080/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]