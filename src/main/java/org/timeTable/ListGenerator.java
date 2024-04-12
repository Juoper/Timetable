package org.timeTable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.course.CourseRepository;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.student.StudentRepository;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;
import java.io.IOException;

@Component
class ListGenerator {
    private final Logger logger = LoggerFactory.getLogger(ListGenerator.class);
    private final CourseRepository courseRepository;
    private PDDocument document;
    private final StudentRepository studentRepository;

    @Autowired
    public ListGenerator(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public void generateCourseList() throws IOException {
        logger.info("Generating course list...");

        document = new PDDocument();

        courseRepository.findAll().forEach(course -> {
            try {
                generatePageForCourse(course);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        document.save("Kurslisten.pdf");
        document.close();

    }

    private void generatePageForCourse(Course course) throws IOException {
        PDPage course_page = new PDPage();
        PDPageContentStream contentStream = new PDPageContentStream(document, course_page);

        contentStream.beginText();
        contentStream.newLineAtOffset(25, 750);
        contentStream.setFont( PDType1Font.TIMES_ROMAN, 20 );
        contentStream.showText("Kursname: " + course.getName());
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Fachkürzel: " + course.getShortSubject());
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Lehrerkürzel: " + course.getTeacher().getAbbreviation());

        contentStream.endText();

        Table.TableBuilder myTableBuilder = Table.builder()
                .addColumnsOfWidth(200, 200)
                .padding(2);

        course.getStudents().forEach(student -> {
            myTableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(student.getPrename()).backgroundColor(Color.WHITE).build())
                    .add(TextCell.builder().text(student.getSurname()).build())
                    .build()
            );
        });

        Table mytable = myTableBuilder.build();

        // Set up the drawer
        TableDrawer tableDrawer = TableDrawer.builder()
                .contentStream(contentStream)
                .startX(20f)
                .startY(course_page.getMediaBox().getUpperRightY() - 120)
                .table(mytable)
                .build();

        // And go for it!
        tableDrawer.draw();

        document.addPage(course_page);
        contentStream.close();

    }

    public void generateStudentCoursesList() throws IOException {
        logger.info("Generating student courses list...");

        document = new PDDocument();

        studentRepository.findAll().forEach(student -> {
            try {
                generatePageForStudent(student);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        document.save("Schülerlisten.pdf");
        document.close();

    }

    private void generatePageForStudent(Student student) throws IOException {
        PDPage course_page = new PDPage();
        PDPageContentStream contentStream = new PDPageContentStream(document, course_page);

        contentStream.beginText();
        contentStream.newLineAtOffset(25, 750);
        contentStream.setFont( PDType1Font.TIMES_ROMAN, 20 );
        contentStream.showText("Schüler: " + student.getPrename() + " " + student.getSurname());
        contentStream.endText();

        Table.TableBuilder myTableBuilder = Table.builder()
                .addColumnsOfWidth(200, 200)
                .padding(2);

        student.getCourses().forEach(course -> {
            myTableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(course.getName()).backgroundColor(Color.WHITE).build())
                    .add(TextCell.builder().text(course.getTeacher().getAbbreviation()).build())
                    .build()
            );
        });

        Table mytable = myTableBuilder.build();

        // Set up the drawer
        TableDrawer tableDrawer = TableDrawer.builder()
                .contentStream(contentStream)
                .startX(20f)
                .startY(course_page.getMediaBox().getUpperRightY() - 120)
                .table(mytable)
                .build();

        // And go for it!
        tableDrawer.draw();

        document.addPage(course_page);
        contentStream.close();
    }
}
