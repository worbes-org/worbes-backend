FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . /app/
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/bootstrap/worbes-api/build/libs/*.jar worbes-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "worbes-api.jar"]
