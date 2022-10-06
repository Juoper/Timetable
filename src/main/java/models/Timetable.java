package models;

import org.apache.commons.lang3.StringUtils;

public class Timetable {
    private Student student;
    public Lesson[][] lessons; // 5 Tage, 11 Stunden


    public Timetable(Student student) {
        this.student = student;
        lessons = new Lesson[5][11];
    }

    public void addLesson(Lesson lesson) {
        lessons[lesson.getDay()][lesson.getHour()] = lesson;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int hour = 0; hour < 11; hour++) {
            for (int day = 0; day < 5; day++) {
                sb.append(StringUtils.center(String.join(" ", lessons[day][hour].getSubject(), lessons[day][hour].getRoom(), lessons[day][hour].getTeacher().getAbbreviation()),30));


            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void transferToDatabase() {
        //TODO
        //table name: student
        //--------
        //id | name

        //table name: course
        //--------
        //name | room | teacher | day | hour

        //table name: student_course
        //--------
        //student_id | course_id

        //before doing this one TODO in Lesson.java has to be done
        //table name: lesson
        //--------
        //course_id | day | hour

        //table name: teacher
        //--------
        //abbreviation | name

    }
}
