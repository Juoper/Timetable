package org.timeTable.models;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Lesson {
    private Course course;
    private String room;
    private int day;
    private int hour;

    private String cellstate;


    public Lesson(String room, int day, int hour) {
        this.room = room;
        this.day = day;
        this.hour = hour;

    }

    public Lesson(int day, int hour) {
        this.day = day;
        this.hour = hour;

    }
    public Lesson(Course course, int day, int hour, String cellstate) {
        this.day = day;
        this.hour = hour;
        this.cellstate = cellstate;
        this.course = course;

    }

    public Course getCourse() {
        return course;
    }

    public String getRoom() {
        return room;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public String toString() {
        return String.format("Day: %d, Hour: %d, Room: %s, cellState: %s", day, hour, room, cellstate);
    }

    public String getCellstate() {
        return cellstate;
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



