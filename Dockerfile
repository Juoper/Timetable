FROM arm64v8/maven:3.9-amazoncorretto-17-debian AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -B package --file pom.xml -DskipTests

FROM openjdk:17
COPY --from=build /workspace/target/*.jar Stundenplan.jar
ENTRYPOINT ["java", "-jar", "Stundenplan.jar"]
