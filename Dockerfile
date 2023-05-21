FROM openjdk:17
WORKDIR /
COPY /target/Stundenplan.jar /Stundenplan.jar
ENTRYPOINT ["java", "-jar", "Stundenplan.jar"]