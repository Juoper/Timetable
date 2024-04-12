package org.timeTable.persistence.lesson;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.timeTable.persistence.course.Course;

@Entity
public class Lesson implements Comparable<Lesson> {

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

    public static int getLessonHour(LocalTime startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        int hour = switch (startTime.format(formatter)) {
            case "08:00" -> 1;
            case "08:45" -> 2;
            case "09:45" -> 3;
            case "10:30" -> 4;
            case "11:35" -> 5;
            case "12:20" -> 6;
            case "13:15" -> 7;
            case "14:00" -> 8;
            case "14:45" -> 9;
            case "15:30" -> 10;
            case "16:15" -> 11;
            default -> 0;
        };


        return hour;
    }

    public String toString() {
        return "Lesson:{" +
                "id=" + id +
                ", course=" + course.getName() +
                ", day='" + day + '\'' +
                ", startTime='" + startTime + '\'' +
                ", cellstate='" + cellstate + '\'' +
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

    @Override
    public int compareTo(@NotNull Lesson o) {
        return getStartTime().compareTo(o.getStartTime());
    }
}



