package org.timeTable.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.lesson.LessonRepository;
import org.timeTable.persistence.teacher.Teacher;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;

    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public Lesson getLessonByCourseAndDayAndHour(Course course, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        List<Lesson> lessons = lessonRepository.findByCourseAndDayAndStartTimeAndEndTime(course, dayOfWeek, startTime, endTime);
        Lesson lesson = null;
        if (lessons == null || lessons.size() == 0) {
            lesson = new Lesson(course, dayOfWeek, startTime, endTime);
            lessonRepository.save(lesson);
        } else {
            lesson = lessons.get(0);
        }
        return lesson;

    }
}
