package models;

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

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
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
