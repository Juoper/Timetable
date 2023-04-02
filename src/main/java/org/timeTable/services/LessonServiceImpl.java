package org.timeTable.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.lesson.LessonRepository;
import org.timeTable.persistence.teacher.Teacher;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class LessonServiceImpl implements LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Override
    public Lesson
    getLessonByCourseAndDayAndHour(Course course, int day, LocalTime startTime, LocalTime endTime) {
        List<Lesson> lessons = lessonRepository.findByCourseAndDayOfWeekAndStartTimeAndEndTime(course, DayOfWeek.of(day), startTime, endTime);
        Lesson lesson = null;
        if (lessons == null || lessons.size() == 0) {
            lesson = new Lesson(course, DayOfWeek.of(day), startTime, endTime);
            lessonRepository.save(teacher);
        } else {
            teacher = lessons.get(0);
        }
        return teacher;
    }
}
