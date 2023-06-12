package org.timeTable.persistence.teacher;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.timeTable.persistence.course.Course;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    private String abbreviation;
    private String prename;
    private String surname;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Course> courses;

    public Teacher(String abbreviation) {
        this.abbreviation = abbreviation;
        this.courses = new ArrayList<>();
    }

    public Teacher() {
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public long getId() {
        return id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getPrename() {
        return prename;
    }

    public String getSurname() {
        return surname;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Teacher teacher) {

            return new EqualsBuilder()
                    .append(abbreviation, teacher.abbreviation)
                    .isEquals();
        } else {
            return false;
        }
    }
}
