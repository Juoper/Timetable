package org.timeTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.course.CourseRepository;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.student.StudentRepository;

import java.io.IOException;
import java.time.ZoneId;

@SpringBootApplication
public class Test {
    public static ZoneId zoneID = ZoneId.of( "Europe/Paris");
    private final Logger logger = LoggerFactory.getLogger(org.timeTable.CommunicationLayer.CommunicationLayer.class);
    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(Test.class, args);

    }

    @Autowired
    public Test(CourseRepository courseRepository, StudentRepository studentRepository) throws IOException {
        logger.info("YOLO");
        Student q12 = studentRepository.findById(158L).get();

        Iterable<Course> courses = courseRepository.findAll();
        courses.forEach(it -> {
            it.addStudent(q12);
            courseRepository.save(it);
            logger.info("saved ");
        });
        logger.info("YOLO finished");


    }
}
