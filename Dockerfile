FROM openjdk:11 AS local
WORKDIR /app
COPY build/libs/*.jar ./app.jar
CMD ["java", "-Dspring.profiles.active=local", "-jar", "app.jar"]



FROM openjdk:11 AS builder
WORKDIR /app
COPY . /app
RUN ./gradlew clean build
COPY build/libs/*.jar /app.jar

FROM builder AS development
WORKDIR /
CMD ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
