package org.timeTable.persistence.student;

import org.timeTable.models.Timetable;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;

import javax.persistence.*;
import java.util.List;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String prename;
    private String surname;
    @ManyToMany
    public List<Course> courses;
    @OneToMany
    private List<Lesson> lessons; // 5 Tage, 11 Stunden

    public Student(String fullname) {
        this.prename = fullname.split(" ")[0];
        this.surname = surname.split(" ", 2)[1];
    }

    public Student() {

    }

    public Long getId() {
        return id;
    }

    public String getPrename() {
        return prename;
    }

    public String getSurname() {
        return surname;
    }

    public String toString() {
        return String.join(" ", prename, surname);
    }

    public List<Course> getCourses() {
        return courses;
    }
}
