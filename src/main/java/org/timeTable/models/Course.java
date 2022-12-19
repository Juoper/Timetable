package org.timeTable.models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    int id;
    private Teacher teacher;
    private String name;
    private String shortSubject;
    private String subject;
    public List<Lesson> lessons;

    public List<Student> students;
    
    public Course(Teacher teacher, String shortSubject, String name) {
        this.id = -1;
        this.lessons = new ArrayList<>();
        this.teacher = teacher;
        this.shortSubject = shortSubject;
        this.name = name;
        this.students = new ArrayList<>();
    }

    public Course(int id, String shortSubject, String name) {
        this.id = id;
        this.lessons = new ArrayList<>();
        this.teacher = null;
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

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public String toString() {
        return "Course{" +
                "id=" + id +
                ", teacher=" + teacher +
                ", name='" + name + '\'' +
                ", shortSubject='" + shortSubject + '\'' +
                ", subject='" + subject + '\'' +
                ", lessons=" + lessons +
                ", students=" + students +
                '}';
    }
}
