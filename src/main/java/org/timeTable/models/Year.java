package org.timeTable.models;

import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.teacher.Teacher;

import java.util.ArrayList;
import java.util.List;

public class Year {
    private List<Course> courses;
    private List<Lesson> lessons; // 5 Tage, 11 Stunden
    private List<Student> students;
    private List<Teacher> teachers;

    public Year() {
        this.courses = new ArrayList<>();
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.teachers = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public Teacher addTeacher(Teacher teacher) {
        if (teachers.stream().noneMatch(t -> t.getAbbreviation().equals(teacher.getAbbreviation()))) {
            teachers.add(teacher);
            return teacher;
        } else {
            return teachers.stream().filter(t -> t.getAbbreviation().equals(teacher.getAbbreviation())).findFirst().get();

        }
    }

    public Lesson addLesson(Lesson lesson) {
        if (lessons.stream().noneMatch(l -> l.getDay() == (lesson.getDay()) && l.getHour() == (lesson.getHour()))) {
            lessons.add(lesson);
            return lesson;
        } else {
            return lessons.stream().filter(l -> l.getDay() == (lesson.getDay()) && l.getHour() == (lesson.getHour())).findFirst().get();
        }
    }


    public Course addCourse(Course course) {
        if (courses.stream().noneMatch(c -> c.getName().equals(course.getName()) && c.getTeacher().getAbbreviation().equals(course.getTeacher().getAbbreviation()))) {
            courses.add(course);
            return course;
        } else {
            return courses.stream().filter(c -> c.getName().equals(course.getName()) && c.getTeacher().getAbbreviation().equals(course.getTeacher().getAbbreviation())).findFirst().get();

        }
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<Student> getStudents() {
        return students;
    }


    public List<Teacher> getTeachers() {
        return teachers;
    }
}
