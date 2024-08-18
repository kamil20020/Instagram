FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /home/app
COPY src ./src
COPY pom.xml .
RUN mvn -f pom.xml package -e -X -Dmaven.test.skip

FROM maven:3.6.3-openjdk-17
WORKDIR /app
COPY --from=build /home/app/target/instagram-0.0.1-SNAPSHOT.jar ./instagram-0.0.1-SNAPSHOT.jar
EXPOSE 9000
ENTRYPOINT ["java","-jar","instagram-0.0.1-SNAPSHOT.jar"]