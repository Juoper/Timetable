package org.timeTable.models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private Teacher teacher;
    private String subject;
    public List<Lesson> lessons;

    public Course(Teacher teacher, String subject) {
        this.lessons = new ArrayList<>();
        this.teacher = teacher;
        this.subject = subject;
    }

    public Lesson addLesson(Lesson lesson) {
        if (lessons.stream().noneMatch(l -> l.getDay() == (lesson.getDay()) && l.getHour() == (lesson.getHour()))) {
            lessons.add(lesson);
            return lesson;
        } else {
            return lessons.stream().filter(l -> l.getDay() == (lesson.getDay()) && l.getHour() == (lesson.getHour())).findFirst().get();
        }
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getSubject() {
        return subject;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }
}
