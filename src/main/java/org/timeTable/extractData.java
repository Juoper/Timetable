package org.timeTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.course.CourseRepository;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.lesson.LessonRepository;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.student.StudentRepository;
import org.timeTable.persistence.teacher.TeacherRepository;
import org.timeTable.services.LessonService;
import org.timeTable.services.StudentService;
import org.timeTable.services.TeacherService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;


@Service
public class extractData {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final StudentService studentService;

    private final Logger logger = LoggerFactory.getLogger(extractData.class);

    @Autowired
    public extractData(CourseRepository courseRepository, LessonRepository lessonRepository, TeacherRepository teacherRepository, TeacherService teacherService, LessonService lessonService, StudentRepository studentRepository, StudentService studentService) {
        this.courseRepository = courseRepository;
        this.teacherService = teacherService;
        this.lessonService = lessonService;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    public void extractFromPdf(String path) throws IOException {
        //Loading an existing document
        File file = new File(path);
        PDDocument document = PDDocument.load(file);
        int pageCount = document.getDocumentCatalog().getPages().getCount();

        for (int i = 0; i < pageCount; i++) {
            generateData(document.getDocumentCatalog().getPages().get(i), i, 0);
            if (i != 78) {
                generateData(document.getDocumentCatalog().getPages().get(i), i, 400);
            }
        }

        document.close();
    }

    public void generateData(PDPage page, int id, int offset) throws IOException {
        PDFTextStripperByArea stripperStudentName = new PDFTextStripperByArea();
        stripperStudentName.setSortByPosition(true);

        Rectangle rectStudentName = new Rectangle(30, 30 + offset, 180, 20);
        stripperStudentName.addRegion("studentName", rectStudentName);
        stripperStudentName.extractRegions(page);

        String studentName = stripperStudentName.getTextForRegion("studentName");
        studentName = studentName.replace("\r\n", "");
        studentName = studentName.trim();

        logger.info("Student: " + studentName);

        Student student = studentService.getOrCreateStudent(studentName);

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);

        prepareStripper(stripper, offset);

        stripper.extractRegions(page);
        for (int day = 0; day < 5; day++) {
            for (int hour = 0; hour < 11; hour++) {
                stripText(day, hour, student, stripper);
            }
        }
        studentRepository.save(student);
    }

    private void stripText(int day, int hour, Student student, PDFTextStripperByArea stripper) {
        String text = stripper.getTextForRegion("class" + day + hour);

        text = text.replaceFirst("^ ", "");
        text = text.replaceFirst("^ \n", "");

        if (text.equals("\n")) {
            text = "Freistunde";
        }

        text = text
                .replace("\n ", "")
                .replace("\n", "");

        //{Kursname} {Kurskürzel} {Lehrer}
        String[] split = text.split(" ");

        if (split.length == 3) {
            String courseShortSubject = removeTypos(split[0]);
            String courseName = removeTypos(split[1]);
            String teacherAbbreviation = removeTypos(split[2]).toUpperCase();

            Course course = getOrCreateCourseByName(courseShortSubject, courseName, teacherAbbreviation);


            student.addCourse(course);
            studentRepository.save(student);

            teacherService.addCourseToTeacher(course.getTeacher(), course);

            LocalTime startTime = coordinateToStartTime(hour);
            LocalTime endTime = startTime.plusMinutes(45);

            Lesson lesson = lessonService.getLessonByCourseAndDayAndHour(course, DayOfWeek.of(day + 1), startTime, endTime);

            course.addStudent(student);
            course.addLesson(lesson);
            courseRepository.save(course);

        }
    }

    private Course getOrCreateCourseByName(String courseShortSubject, String courseName, String teacherAbbreviation) {
        //TODO move to courseService
        List<Course> coursesByName = courseRepository.findByName(courseName);
        Course course = null;
        if (coursesByName == null || coursesByName.size() == 0) {
            course = new Course(teacherService.getOrCreateTeacherByAbbreviation(teacherAbbreviation), courseName, courseShortSubject);
            course = courseRepository.save(course);
        } else if (coursesByName.size() == 1) {
            course = coursesByName.get(0);

        } else {
            logger.error("More than one course with the same abbreviation found: " + courseName);
        }

        return course;
    }

    private void prepareStripper(PDFTextStripperByArea stripper, int offset) {
        for (int hour = 0; hour < 11; hour++) {
            for (int day = 0; day < 5; day++) {
                Rectangle rect = new Rectangle(120 + 90 * day, 72 + offset + (30 * hour), 91, 30);
                stripper.addRegion("class" + day + hour, rect);
            }
        }
    }

    public String removeTypos(String text) {
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

    private LocalTime coordinateToStartTime(int hour) {
        LocalTime startTime = LocalTime.of(8, 0);

        switch (hour + 1) {
            case 1:
                startTime = LocalTime.of(8, 0);
                break;
            case 2:
                startTime = LocalTime.of(8, 45);
                break;
            case 3:
                startTime = LocalTime.of(9, 45);
                break;
            case 4:
                startTime = LocalTime.of(10, 30);
                break;
            case 5:
                startTime = LocalTime.of(11, 35);
                break;
            case 6:
                startTime = LocalTime.of(12, 20);
                break;
            case 7:
                startTime = LocalTime.of(13, 15);
                break;
            case 8:
                startTime = LocalTime.of(14, 00);
                break;
            case 9:
                startTime = LocalTime.of(14, 45);
                break;
            case 10:
                startTime = LocalTime.of(15, 30);
                break;
            case 11:
                startTime = LocalTime.of(16, 15);
                break;
            default:
                throw new IllegalArgumentException("Invalid hour: " + hour);
        }
        return startTime;
    }

}
