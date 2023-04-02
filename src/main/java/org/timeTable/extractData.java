//package org.timeTable;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.timeTable.CommunicationLayer.CommunicationLayer;
//import org.timeTable.models.*;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//
//import org.apache.pdfbox.text.PDFTextStripperByArea;
//import org.timeTable.persistence.course.Course;
//import org.timeTable.persistence.course.CourseRepository;
//import org.timeTable.persistence.lesson.Lesson;
//import org.timeTable.persistence.lesson.LessonRepository;
//import org.timeTable.persistence.student.Student;
//import org.timeTable.persistence.teacher.Teacher;
//import org.timeTable.persistence.teacher.TeacherRepository;
//import org.timeTable.services.LessonService;
//import org.timeTable.services.TeacherService;
//
//import java.awt.*;
//import java.io.File;
//import java.io.IOException;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//
//@Service
//public class extractData {
//
//    @Autowired
//    CourseRepository courseRepository;
//    @Autowired
//    LessonRepository lessonRepository;
//    @Autowired
//    TeacherRepository teacherRepository;
//
//    @Autowired
//    TeacherService teacherService;
//    @Autowired
//    LessonService lessonService;
//
//    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);
//
//    public static void extractFromPdf(String path) throws IOException {
//        //Loading an existing document
//        File file = new File(path);
//        PDDocument document = PDDocument.load(file);
//        int pageCount = document.getDocumentCatalog().getPages().getCount();
//
//        for (int i = 0; i < pageCount; i++) {
//            extractData.generateData(document.getDocumentCatalog().getPages().get(i), i);
//        }
//
//        document.close();
//    }
//
//    public static void generateData(PDPage page, int id) throws IOException {
//        PDFTextStripperByArea stripperStudentName = new PDFTextStripperByArea();
//        stripperStudentName.setSortByPosition(true);
//
//        Rectangle rectStudentName = new Rectangle(180, 3609 - 3450, 705, 50);
//        stripperStudentName.addRegion("studentName", rectStudentName);
//        stripperStudentName.extractRegions(page);
//
//        String studentName = stripperStudentName.getTextForRegion("studentName");
//        studentName = studentName.replace("\r\n", "");
//        studentName = studentName.trim();
//
//
//        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
//        stripper.setSortByPosition(true);
//
//        prepareStripper(stripper);
//
//        stripper.extractRegions(page);
//
//        for (int hour = 0; hour < 11; hour++) {
//            for (int day = 0; day < 5; day++) {
//
//            }
//        }
//
//        Student student = new Student(studentName);
//
//    }
//
//    private void stripText(int x, int y, PDFTextStripperByArea stripper) {
//        String text = stripper.getTextForRegion("class" + x + y);
//
//        text = text.replaceFirst("^ ", "");
//        text = text.replaceFirst("^ \r\n", "");
//
//        if (text.equals("\r\n")) {
//            text = "Freistunde";
//        }
//
//        text = text
//                .replace("\r\n ", "")
//                .replace("\r\n", "");
//
//
//        //{Kursname} {Kurskürzel} {Lehrer}
//        String[] split = text.split(" ");
//
//
//        if (split.length == 3) {
//            split[0] = removeTypos(split[0]);
//            split[1] = removeTypos(split[1]);
//            split[2] = removeTypos(split[2]);
//
//
//            String courseShortSubject = split[0];
//            String courseAbbreviation = split[1];
//            String teacherAbbreviation = split[2].toUpperCase();
//
//            if (teacherAbbreviation.equals("ISE") && courseAbbreviation.equals("QWU")) {
//                courseAbbreviation = "QWU_ISE";
//            }
//
//            if (split[1].equals("Smw/P1/")) {
//                split[1] = split[1] + split[2];
//            }
//
//            Course course = getCourseByShortSubject(courseShortSubject, courseAbbreviation, teacherAbbreviation);
//            teacherService.addCourseToTeacher(course.getTeacher(), course);
//            Lesson lesson = lessonService.createLe
//
//
//
//            Teacher teacher = new Teacher(split[2]);
//
//            teacher = year.addTeacher(teacher);
//
//            Course course = new Course(teacher, split[0], split[1]);
//            lesson.setCourse(course);
//
//            lesson = year.addLesson(lesson);
//            course = year.addCourse(course);
//
//            timetable.addCourse(course);
//
//            course.addLesson(lesson);
//            course.addStudent(student);
//
//        }
//    }
//
//    private Course getCourseByShortSubject(String courseShortSubject, String courseAbbreviation, String teacherAbbreviation) {
//        //TODO move to courseService
//        List<Course> coursesByAbbreviation = courseRepository.findByShortSubject(courseAbbreviation);
//        Course course = null;
//        if (coursesByAbbreviation == null || coursesByAbbreviation.size() == 0) {
//            course = new Course(teacherService.getTeacherByAbbreviation(teacherAbbreviation), courseShortSubject, courseAbbreviation);
//        } else if (coursesByAbbreviation.size() == 1) {
//            course = coursesByAbbreviation.get(0);
//        } else {
//            logger.error("More than one course with the same abbreviation found: " + courseAbbreviation);
//        }
//
//        return course;
//    }
//
//
//    private static void prepareStripper(PDFTextStripperByArea stripper) {
//        for (int hour = 0; hour < 11; hour++) {
//            for (int day = 0; day < 5; day++) {
//                Rectangle rect = new Rectangle(500 + 385 * day, 309 + 130 * hour, 385, 130);
//                stripper.addRegion("class" + day + hour, rect);
//            }
//        }
//    }
//
//    public static String removeTypos(String text) {
//
//
//        return text
//
//                .replace("|GOE", "GOE")
//                .replace("| GOE", " GOE")
//                .replace(" c ", " C ")
//                .replace("|", "")
//                .replace("g9e0", "geo")
//                .replace("QAWU", "QWU")
//                .replace("1m?", "1m7")
//                .replace("Kw", "Ku/")
//                .replace("9", "g")
//                .replace("14", "1d")
//                .replace(" 6", " C")
//                .replace("/w", "/W")
//                .replace("I/", "/")
//                .replace("Cc", "C")
//                .replace("Smwi", "Smw")
//                .replace("Wi", "W1")
//                .replace("Kuw", "Ku")
//                .replace("WRI", "WR")
//                .replace("Ww", "W")
//                .replace("Kk", "k")
//                .replace("kK", "k")
//                .replace("1K3", "1k3")
//                .replace("Twr", "wr")
//                .replace("PA", "P1")
//                .replace("/p", "/P")
//                .replace("/0", "/O")
//                .replace("KuP", "Ku/P")
//                .replace("ge0", "geo")
//                .replace("15-11", "1s-t1")
//                .replace("MIW", "M/W")
//                .replace("_psylps2", "psy/ps2")
//                .replace("Pi", "P1")
//                .replace("/Ps", "/ps")
//                .replace("1smw7?", "1smw7")
//                .replace("7?", "7")
//                .replace("?", "")
//                .replace("iwr", "1wr")
//                .replace("185", "1e5")
//                .replace("TurF", "Tuf")
//                .replace("OO", "OE")
//                .replace("]", "")
//                .replace("voS", "vo5")
//                .replace("GW1", "G/W1")
//                .replace("Tuf", "TuF")
//                .replace("Tur", "TuF")
//                .replace("wS", "w5")
//                .replace("//", "/")
//                .replace("1smw ", "1smw7 ")
//                .replace("/OE", "/O")
//                .replace("HAl", "HA")
//                .replace("HE)", "HE")
//                .replace("nSm", "n Sm")
//                .replace("WEL", "WEI")
//                //.replace("1fot1", "1fo")
//                ;
//    }
//
//
//}
