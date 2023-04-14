package org.timeTable.persistence.course;

import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.teacher.Teacher;
import org.timeTable.persistence.lesson.Lesson;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    int untisId;
    @ManyToOne
    private Teacher teacher;
    private String name;
    private String shortSubject;
    private String subject;
    @OneToMany
    public List<Lesson> lessons;
    @ManyToMany
    public List<Student> students;

    public Course() {
    }

    public Course(Teacher teacher, String name, String shortSubject) {
        this.teacher = teacher;
        this.name = name;
        this.shortSubject = shortSubject;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public Student addStudent(Student student) {
        if (students.stream().noneMatch(s -> s.getPrename().equals(student.getPrename()) && s.getSurname().equals(student.getSurname()))) {
            students.add(student);
            return student;
        } else {
            return students.stream().filter(s -> s.getPrename().equals(student.getPrename()) && s.getSurname().equals(student.getSurname())).findFirst().get();
        }
    }

    public long getId() {
        return id;
    }

    public int getUntisId() {
        return untisId;
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
                ", untisId=" + untisId +
                '}';
    }
}
