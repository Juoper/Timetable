package org.timeTable;

import org.apache.commons.lang3.StringUtils;
import org.timeTable.models.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;


public class extractData {
    public static Year year = new Year();

    //To Do:
    //replace Ku  Ku/P1/HA with Ku Ku/P1/HA aka remove one withspace

    public static Year extractFromPdf(String path) throws IOException {
        //Loading an existing document
        File file = new File(path);
        PDDocument document = PDDocument.load(file);
        int pageCount = document.getDocumentCatalog().getPages().getCount();

        for (int i = 0; i < pageCount; i++) {
            extractData.generateYear(document.getDocumentCatalog().getPages().get(i), i);
        }

        document.close();

        return year;
    }

    public static void generateYear(PDPage page, int id) throws IOException {
        PDFTextStripperByArea stripperStudentName = new PDFTextStripperByArea();
        stripperStudentName.setSortByPosition(true);

        Rectangle rectStudentName = new Rectangle(180, 3609 - 3450, 705, 50);
        stripperStudentName.addRegion("studentName", rectStudentName);
        stripperStudentName.extractRegions(page);

        String studentName = stripperStudentName.getTextForRegion("studentName");
        studentName = studentName.replace("\r\n", "");
        studentName = studentName.trim();

        Student student = new Student(id + 1, studentName);

        year.addStudent(student);
        Timetable timetable = new Timetable(student);

        student.addTimetable(timetable);

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);

        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 5; x++) {
                Rectangle rect = new Rectangle(500 + 385 * x, 309 + 130 * y, 385, 130);
                stripper.addRegion("class" + x + y, rect);
            }
        }

        stripper.extractRegions(page);

        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 5; x++) {
                String text = stripper.getTextForRegion("class" + x + y);

                text = text.replaceFirst("^ ", "");
                text = text.replaceFirst("^ \r\n", "");

                if (text.equals("\r\n")) {
                    text = "Freistunde";
                }

                text = text
                        .replace("\r\n ", "")
                        .replace("\r\n", "");


                String[] split = text.split(" ");


                if (split.length == 3) {
                    split[2] = split[2].toUpperCase();

                    split[0] = removeTypos(split[0]);
                    split[1] = removeTypos(split[1]);
                    split[2] = removeTypos(split[2]);

                    if (split[2].equals("ISE") && split[1].equals("QWU")) {
                        split[1] = "QWU_ISE";
                    }

                    if (split[1].equals("Smw/P1/")) {
                        split[1] = split[1] + split[2];
                    }

                    Lesson lesson = new Lesson(x, y);
                    Teacher teacher = new Teacher(split[2]);

                    teacher = year.addTeacher(teacher);

                    Course course = new Course(teacher, split[0], split[1]);


                    lesson = year.addLesson(lesson);
                    course = year.addCourse(course);

                    timetable.addCourse(course);

                    course.addLesson(lesson);
                    course.addStudent(student);
                    timetable.addLesson(lesson);

                } else {
                    Teacher teacher = new Teacher("free");
                    teacher = year.addTeacher(teacher);

                    Lesson lesson = new Lesson(x, y);

                    Course course = new Course(teacher, split[0], "free");
                    lesson = year.addLesson(lesson);
                    course = year.addCourse(course);

                    timetable.addCourse(course);

                    course.addLesson(lesson);
                    course.addStudent(student);

                    timetable.addLesson(lesson);
                }
            }
        }
    }

    public static String removeTypos(String text) {
        

        return text

                .replace("|GOE", "GOE")
                .replace("| GOE", " GOE")
                .replace(" c ", " C ")
                .replace("|", "")
                .replace("g9e0", "geo")
                .replace("QAWU", "QWU")
                .replace("1m?", "1m7")
                .replace("Kw", "Ku/")
                .replace("9", "g")
                .replace("14", "1d")
                .replace(" 6", " C")
                .replace("/w", "/W")
                .replace("I/", "/")
                .replace("Cc", "C")
                .replace("Smwi", "Smw")
                .replace("Wi", "W1")
                .replace("Kuw", "Ku")
                .replace("WRI", "WR")
                .replace("Ww", "W")
                .replace("Kk", "k")
                .replace("kK", "k")
                .replace("1K3", "1k3")
                .replace("Twr", "wr")
                .replace("PA", "P1")
                .replace("/p", "/P")
                .replace("/0", "/O")
                .replace("KuP", "Ku/P")
                .replace("ge0", "geo")
                .replace("15-11", "1s-t1")
                .replace("MIW", "M/W")
                .replace("_psylps2", "psy/ps2")
                .replace("Pi", "P1")
                .replace("/Ps", "/ps")
                .replace("1smw7?", "1smw7")
                .replace("7?", "7")
                .replace("?", "")
                .replace("iwr", "1wr")
                .replace("185", "1e5")
                .replace("TurF", "Tuf")
                .replace("OO", "OE")
                .replace("]", "")
                .replace("voS", "vo5")
                .replace("GW1", "G/W1")
                .replace("Tuf", "TuF")
                .replace("Tur", "TuF")
                .replace("wS", "w5")
                .replace("//", "/")
                .replace("1smw ", "1smw7 ")
                .replace("/OE", "/O")
                .replace("HAl", "HA")
                .replace("HE)", "HE")
                .replace("nSm", "n Sm")
                .replace("WEL", "WEI")
                //.replace("1fot1", "1fo")
                ;
    }


    public void save() throws IOException {
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
    }

}
