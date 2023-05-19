package org.timeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.timeTable.communicationLayer.CommunicationLayer;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.course.CourseRepository;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.student.StudentRepository;

import java.time.ZoneId;
import java.util.ArrayList;


@SpringBootApplication
public class Main {
    public static ZoneId zoneID = ZoneId.of( "Europe/Paris");
    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);
    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(Main.class, args);
    }

    @Autowired
    public Main(CommunicationLayer communicationLayer, StudentRepository studentRepository, CourseRepository courseRepository) {
        communicationLayer.startTimers();

    }
}
