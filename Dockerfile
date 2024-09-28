FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /home/app
COPY pom.xml pom.xml
COPY ./src ./src
RUN mvn package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /home/app
COPY --from=build /home/app/target/receiver-0.0.1-SNAPSHOT.jar ./receiver-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "receiver-0.0.1-SNAPSHOT.jar"]