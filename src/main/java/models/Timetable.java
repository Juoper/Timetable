package models;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private Student student;

    public List<Course> courses;
    private Lesson[][] lessons; // 5 Tage, 11 Stunden

    public Timetable(Student student) {
        this.student = student;
        courses = new ArrayList<>();
        lessons = new Lesson[5][11];
    }

    public void addLesson(Lesson lesson) {
        lessons[lesson.getDay()][lesson.getHour()] = lesson;
    }

    public Course newCourse(Course course) {
        if (courses.stream().noneMatch(c -> c.getSubject().equals(course.getSubject()))) {
            courses.add(course);
            return course;
        } else {
            return courses.stream().filter(c -> c.getSubject().equals(course.getSubject())).findFirst().get();

        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        //TODO fix to string
        for (int hour = 0; hour < 11; hour++) {
            for (int day = 0; day < 5; day++) {

                Lesson lesson = lessons[day][hour];

                Course course = new Course(null, "test");

                if ( courses.stream().anyMatch(c -> c.getLessons().contains(lesson))){
                    course = courses.stream().filter(c -> c.getLessons().contains(lesson)).findFirst().get();
                }
                sb.append(StringUtils.center(String.join(" ", course.getSubject(), lessons[day][hour].getRoom(), course.getTeacher().getAbbreviation()),30));


            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void transferToDatabase() {


    }

    public void print() {
        System.out.println("test");
        courses.forEach(c -> {
            System.out.println("1");
            c.lessons.forEach(System.out::println);
        });
    }
}
