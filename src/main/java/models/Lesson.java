package models;

public class Lesson {
    //TODO: remove teacher, subject, room and move it to course
    private Teacher teacher;
    private String subject;
    private String room;
    private int day;
    private int hour;

    public Lesson(Teacher teacher, String subject, String room, int day, int hour) {
        this.teacher = teacher;
        this.subject = subject;
        this.room = room;
        this.day = day;
        this.hour = hour;

    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getSubject() {
        return subject;
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
}
