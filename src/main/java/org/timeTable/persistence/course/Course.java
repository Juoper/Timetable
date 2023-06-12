package org.timeTable.persistence.course;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.teacher.Teacher;
import org.timeTable.persistence.lesson.Lesson;

import java.util.*;


@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    private int untisId;
    @ManyToOne
    private Teacher teacher;
    private String name;
    private String shortSubject;
    private String subject;
    @OneToMany
    public Set<Lesson> lessons;
    @ManyToMany
    private List<Student> students;

    public Course() {
    }

    public Course(Teacher teacher, String name, String shortSubject) {
        this.teacher = teacher;
        this.name = name;
        this.shortSubject = shortSubject;
        this.lessons = new HashSet<Lesson>();
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        if (students.contains(student)) {
            return;
        }
            students.add(student);
    }
    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
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

    public Set<Lesson> getLessons() {
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
                ", teacher=" + teacher.getAbbreviation() +
                ", name='" + name + '\'' +
                ", shortSubject='" + shortSubject + '\'' +
                ", subject='" + subject + '\'' +
                ", untisId=" + untisId +
                '}';
    }

    public void setUntisId(int untisId) {
        this.untisId = untisId;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortSubject(String shortSubject) {
        this.shortSubject = shortSubject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(name)
                .append(teacher)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Course course) {

            return new EqualsBuilder()
                    .append(name, course.name)
                    .append(teacher.getAbbreviation(), course.teacher.getAbbreviation())
                    .isEquals();
        } else {
            return false;
        }
    }

}
