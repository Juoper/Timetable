package org.timeTable.persistence.lesson;

import org.springframework.data.repository.CrudRepository;
import org.timeTable.persistence.course.Course;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    List<Lesson> findByCourseAndDayOfWeekAndStartTimeAndEndTime(Course course, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
}
