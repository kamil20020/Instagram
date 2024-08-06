FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /home/app
COPY src /src
COPY pom.xml .
RUN mvn -f pom.xml package

FROM maven:3.8.3-openjdk-17
COPY --from=build /target/instagram-0.0.1-SNAPSHOT.jar instagram-0.0.1-SNAPSHOT.jar
EXPOSE 9000
ENTRYPOINT ["java","-jar","instagram-0.0.1-SNAPSHOT.jar"]