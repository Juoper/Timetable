package org.timeTable.services;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;

import java.time.LocalTime;

@Repository
public interface LessonService {
    Lesson getLessonByCourseAndDayAndHour(Course course, int day, LocalTime startTime, LocalTime endTime);
}
