FROM openjdk:17
WORKDIR /
COPY target/Stundenplan.jar Stundenplan.jar
CMD ["java", "-jar", "Stundenplan.jar"]