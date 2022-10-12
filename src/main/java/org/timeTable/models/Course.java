package org.timeTable.models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private Teacher teacher;
    private String name;
    private String shortSubject;
    private String subject;
    public List<Lesson> lessons;

    public List<Student> students;

    public Course(Teacher teacher, String shortSubject, String name) {
        this.lessons = new ArrayList<>();
        this.teacher = teacher;
        this.shortSubject = shortSubject;
        this.name = name;
        this.students = new ArrayList<>();
    }

    public Lesson addLesson(Lesson lesson) {
        if (lessons.stream().noneMatch(l -> l.getDay() == (lesson.getDay()) && l.getHour() == (lesson.getHour()))) {
            lessons.add(lesson);
            return lesson;
        } else {
            return lessons.stream().filter(l -> l.getDay() == (lesson.getDay()) && l.getHour() == (lesson.getHour())).findFirst().get();
        }
    }

    public Student addStudent(Student student) {
        if (students.stream().noneMatch(s -> s.getPrename().equals(student.getPrename()) && s.getSurname().equals(student.getSurname()))) {
            students.add(student);
            return student;
        } else {
            return students.stream().filter(s -> s.getPrename().equals(student.getPrename()) && s.getSurname().equals(student.getSurname())).findFirst().get();
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

    public String getShortSubject() {
        return shortSubject;
    }

    public String getName() {
        return name;
    }

    public List<Student> getStudents() {
        return students;
    }
}
