package org.timeTable.models;

import org.apache.commons.lang3.StringUtils;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.student.Student;

import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private Student student;
    private List<Course> courses;
    private Lesson[][] lessons; // 5 Tage, 11 Stunden

    public Timetable(Student student) {
        this.student = student;
        courses = new ArrayList<>();
        lessons = new Lesson[5][11];
    }

    public void addLesson(Lesson lesson) {
        lessons[lesson.getDay()][lesson.getHour()] = lesson;
    }

    public Course addCourse(Course course) {
        if (courses.stream().noneMatch(c -> c.getName().equals(course.getName()) && c.getTeacher().getAbbreviation().equals(course.getTeacher().getAbbreviation()))) {
            courses.add(course);
            return course;
        } else {
            return courses.stream().filter(c -> c.getName().equals(course.getName()) && c.getTeacher().getAbbreviation().equals(course.getTeacher().getAbbreviation())).findFirst().get();

        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        //TODO fix to string
        for (int hour = 0; hour < 11; hour++) {
            for (int day = 0; day < 5; day++) {

                Lesson lesson = lessons[day][hour];

                Course course = new Course(null, "error", "000");

                if ( courses.stream().anyMatch(c -> c.getLessons().contains(lesson))){
                    course = courses.stream().filter(c -> c.getLessons().contains(lesson)).findFirst().get();
                }
                sb.append(StringUtils.center(String.join(" ", course.getName(), course.getTeacher().getAbbreviation()),20));


            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void transferToDatabase() {

    }

    public List<Course> getCourses() {
        return courses;
    }

    

    public void print() {
        System.out.println("test");
        courses.forEach(c -> {
            System.out.println("1");
            c.lessons.forEach(System.out::println);
        });
    }
}
