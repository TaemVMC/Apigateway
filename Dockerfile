FROM openjdk:11 AS local
WORKDIR /app
COPY build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]


FROM openjdk:11 AS builder
WORKDIR /app
COPY . /app
RUN ./gradlew clean build


FROM builder AS development
WORKDIR /app
CMD ["java", "-jar", "build/libs/Apigateway-0.0.1-SNAPSHOT.jar"]

#FROM openjdk:11-jre-slim AS production
#WORKDIR /
#COPY --from=builder /app.jar /app.jar
#CMD ["java", "-jar", "app.jar"]
