FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /home/app
COPY pom.xml .
COPY ./src ./src
RUN mvn package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /home/app
COPY --from=build /home/app/target/chat-0.0.1-SNAPSHOT.jar ./chat-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "chat-0.0.1-SNAPSHOT.jar"]