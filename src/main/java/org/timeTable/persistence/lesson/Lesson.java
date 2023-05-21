package org.timeTable.persistence.lesson;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.timeTable.persistence.course.Course;
import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Course course;
    private String room;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    @Transient
    private String cellstate;

    public Lesson() {
    }

    public Lesson(Course course, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.course = course;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Lesson(Course course, DayOfWeek day, LocalTime startTime, LocalTime endTime, String cellstate) {
        this.course = course;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cellstate = cellstate;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public String getCellstate() {
        return cellstate;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String toString() {
        return "Lesson:{" +
                ", startTime='" + startTime +
                ", cellstate='" + cellstate +
                '}';
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(startTime)
                .append(endTime)
                .append(day)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Lesson lesson) {
            return new EqualsBuilder()
                    .append(id, lesson.id)
                    .append(day, lesson.day)
                    .append(startTime, lesson.startTime)
                    .append(endTime, lesson.endTime)
                    .isEquals();
        } else {
            return false;
        }
    }
}



