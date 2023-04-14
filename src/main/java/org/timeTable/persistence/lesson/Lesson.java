package org.timeTable.persistence.lesson;

import org.timeTable.persistence.course.Course;
import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
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

    public static int getLessonHour(int startTime) {
        int hour = 0;

        switch (startTime){
            case 800: hour = 1; break;
            case 845: hour = 2; break;
            case 945: hour = 3; break;
            case 1030: hour = 4; break;
            case 1135: hour = 5; break;
            case 1220: hour = 6; break;
            case 1315: hour = 7; break;
            case 1400: hour = 8; break;
            case 1445: hour = 9; break;
            case 1530: hour = 10; break;
            case 1615: hour = 11; break;
        }


        return hour;
    }
}



