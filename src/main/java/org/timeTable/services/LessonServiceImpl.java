package org.timeTable.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.lesson.LessonRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);


    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public Lesson getLessonByCourseAndDayAndHour(Course course, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        List<Lesson> lessons = lessonRepository.findByCourseAndDayAndStartTimeAndEndTime(course, dayOfWeek, startTime, endTime);
        Lesson lesson;
        if (lessons == null || lessons.size() == 0) {
            lesson = new Lesson(course, dayOfWeek, startTime, endTime);
            lesson = lessonRepository.save(lesson);
        } else {
            lesson = lessons.get(0);
            if (lesson == null){
                throw new IllegalArgumentException("Lesson is null");
            }
        }
        return lesson;

    }
}
