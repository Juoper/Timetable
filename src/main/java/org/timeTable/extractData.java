package org.timeTable;

import org.timeTable.models.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
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

        Student student = new Student(id, studentName);

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
                text = text.replace("\r\n ", "");
                text = text.replace("\r\n", "");
                text = text.replace("|GOE", "GOE");
                text = text.replace("| GOE", " GOE");

                String[] split = text.split(" ");



                if (split.length == 3) {
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
}
