package org.timeTable.persistence.teacher;

import org.timeTable.persistence.course.Course;

import javax.persistence.*;
import java.util.List;

@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    private String abbreviation;
    private String prename;
    private String surname;

    @OneToMany
    private List<Course> courses;

    public Teacher(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
