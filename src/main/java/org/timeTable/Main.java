package org.timeTable;

import org.apache.commons.text.StringEscapeUtils;
import org.timeTable.models.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Main {
    //TODO
    //Change every name to prename + surname

    public LiteSQL liteSQL;

    public static void main(String[] args) throws IOException {
        LiteSQL.connect();

        Database.createTables();
        Year year = extractData.extractFromPdf("Stundenplan.pdf");        //Julian_Stundenplan.pdf, JC_Stundenplan.pdf
        LiteSQL.onUpdate("DELETE FROM course");
        LiteSQL.onUpdate("DELETE FROM sqlite_sequence WHERE name='course'");
        LiteSQL.onUpdate("DELETE FROM teacher");
        LiteSQL.onUpdate("DELETE FROM sqlite_sequence WHERE name='teacher'");
        LiteSQL.onUpdate("DELETE FROM lesson");
        LiteSQL.onUpdate("DELETE FROM sqlite_sequence WHERE name='lesson'");
        LiteSQL.onUpdate("DELETE FROM student");
        LiteSQL.onUpdate("DELETE FROM sqlite_sequence WHERE name='student'");
        LiteSQL.onUpdate("DELETE FROM course_lesson");
        LiteSQL.onUpdate("DELETE FROM sqlite_sequence WHERE name='course_lesson'");
        LiteSQL.onUpdate("DELETE FROM course_teacher");
        LiteSQL.onUpdate("DELETE FROM sqlite_sequence WHERE name='course_teacher'");
        LiteSQL.onUpdate("DELETE FROM student_course");
        LiteSQL.onUpdate("DELETE FROM sqlite_sequence WHERE name='student_course'");


        year.getTeachers().forEach(teacher -> {
            try {
                //, prename, surname
                ResultSet set = LiteSQL.onQuery("INSERT INTO teacher (abbreviation) VALUES ('" + teacher.getAbbreviation() + "') RETURNING id");
                set.next();
                teacher.setId(Integer.parseInt(set.getString("id")));
                set.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        year.getCourses().forEach(course -> {

            try {
                ResultSet set = LiteSQL.onQuery("INSERT INTO course (name, subject, shortsubject) " + "VALUES ('" + course.getName() + "', '" + course.getSubject() + "', '" + course.getShortSubject() + "') RETURNING id");
                int id = set.getInt("id");

                course.setId(id);
                set.close();

                LiteSQL.onUpdate("INSERT INTO course_teacher (course_id, teacher_id) " +
                        "VALUES (" + id + ", " + course.getTeacher().getId() + ")");

                course.getStudents().forEach(student -> {
                    LiteSQL.onUpdate("INSERT INTO student_course (student_id, course_id) " +
                            "VALUES (" + student.getId() + ", " + id + ")");
                });


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            course.getLessons().forEach(lesson -> {

                try {
                    ResultSet set = LiteSQL.onQuery("INSERT INTO lesson (day, hour) VALUES ('" + lesson.getDay() + "', '" + lesson.getHour() + "') RETURNING id");
                    int id = set.getInt("id");
                    set.close();

                    LiteSQL.onUpdate("INSERT INTO course_lesson (course_id, lesson_id) " +
                            "VALUES (" + id + ", " + course.getId() + ")");

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        year.getStudents().forEach(student -> {
            LiteSQL.onUpdate("INSERT INTO student (prename, surname) VALUES ('" + student.getPrename() + "', '" + student.getSurname() + "')");
        });


        LiteSQL.disconnect();
    }

}
