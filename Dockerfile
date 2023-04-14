FROM openjdk:18
WORKDIR /
ADD Stundenplan-1.0-SNAPSHOT.jar Stundenplan-1.0-SNAPSHOT.jar
ADD datenbank.db /data/datenbank.db
ADD Stundenplan.config /data/Stundenplan.config
CMD ["java", "-jar", "Stundenplan-1.0-SNAPSHOT.jar"]