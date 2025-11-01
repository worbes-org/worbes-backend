FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . /app/
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/bootstrap/worbes-batch/build/libs/*.jar worbes-batch.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-XX:+UseContainerSupport", "-XX:MaxMetaspaceSize=256m", "-XX:+ExitOnOutOfMemoryError", "-jar", "worbes-batch.jar"]
