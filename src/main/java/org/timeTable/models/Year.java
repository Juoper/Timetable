package org.timeTable.models;

import java.util.ArrayList;
import java.util.List;

public class Year {
    private List<Course> courses;
    private List<Lesson> lessons; // 5 Tage, 11 Stunden
    private List<Student> students;

    public Year() {
        this.courses = new ArrayList<>();
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public Lesson addLesson(Lesson lesson) {
        if (lessons.stream().noneMatch(l -> l.getDay() == (lesson.getDay()) && l.getHour() == (lesson.getHour()))) {
            lessons.add(lesson);
            return lesson;
        } else {
            return lessons.stream().filter(l -> l.getDay() == (lesson.getDay()) && l.getHour() == (lesson.getHour())).findFirst().get();
        }
    }

    public Course addCourse(Course course) {
        if (courses.stream().noneMatch(c -> c.getSubject().equals(course.getSubject()))) {
            courses.add(course);
            return course;
        } else {
            return courses.stream().filter(c -> c.getSubject().equals(course.getSubject())).findFirst().get();

        }
    }
}
