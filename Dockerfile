FROM openjdk:17
WORKDIR /
ADD Stundenplan.jar Stundenplan.jar
CMD ["java", "-jar", "Stundenplan.jar"]