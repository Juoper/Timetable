package org.timeTable.models;

public class Lesson {
    private Course course;
    private String room;
    private int day;
    private int hour;


    public Lesson(String room, int day, int hour) {
        this.room = room;
        this.day = day;
        this.hour = hour;

    }

    public Lesson(int day, int hour) {
        this.day = day;
        this.hour = hour;

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
        return String.format("Day: %d, Hour: %d, Room: %s", day, hour, room);
    }
}
