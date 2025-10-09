FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . /app/
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/bootstrap/worbes-batch/build/libs/*.jar worbes-batch.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "worbes-batch.jar"]
