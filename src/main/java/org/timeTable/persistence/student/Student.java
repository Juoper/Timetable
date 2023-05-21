package org.timeTable.persistence.student;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.timeTable.persistence.course.Course;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String prename;
    private String surname;
    @ManyToMany(mappedBy = "students")
    public List<Course> courses;
    public Student(String prename, String surname) {
        this.prename = prename;
        this.surname = surname;
        this.courses = new ArrayList<>();
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

    public void addCourse(Course course) {
        if (courses.contains(course)) {
            return;
        }
        courses.add(course);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(prename)
                .append(surname)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Student student) {

            return new EqualsBuilder()
                    .append(prename, student.prename)
                    .append(surname, student.surname)
                    .isEquals();
        } else {
            return false;
        }
    }
}
