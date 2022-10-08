import models.Lesson;
import models.Student;
import models.Teacher;
import models.Timetable;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class extractData {

    //To Do:
    //replace Ku  Ku/P1/HA with Ku Ku/P1/HA aka remove one withspace

    public static List<Timetable> extractFromPdf(String path) throws IOException {
        //Loading an existing document

        File file = new File(path);
        PDDocument document = PDDocument.load(file);
        int pageCount = document.getDocumentCatalog().getPages().getCount();

        List<Timetable> timetables = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            timetables.add(extractData.generateTimetable(document.getDocumentCatalog().getPages().get(i), i));
        }

        document.close();

        return timetables;
    }

    public static Timetable generateTimetable(PDPage page, int id) throws IOException {

        PDFTextStripperByArea stripperStudentName = new PDFTextStripperByArea();
        stripperStudentName.setSortByPosition(true);

        Rectangle rectStudentName = new Rectangle(180, 3609 - 3450, 705, 40);
        stripperStudentName.addRegion("studentName", rectStudentName);
        stripperStudentName.extractRegions(page);

        Student student = new Student(id, stripperStudentName.getTextForRegion("studentName"));


        Timetable timetable = new Timetable(student);

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

                text = text.replaceFirst("^ \r\n", "");
                text = text.replaceFirst("^ ", "");

                if (text.equals("\r\n")) {
                    text = "Freistunde";
                }
                text = text.replace("\r\n ", "");
                text = text.replace("\r\n", "");

                String[] split = text.split(" ");

                if (split.length == 3) {
                    Teacher teacher = new Teacher(split[2]);
                    Lesson lesson = new Lesson(teacher, split[0], split[1], x, y);
                    timetable.addLesson(lesson);
                } else {
                    Teacher teacher = new Teacher(null);
                    Lesson lesson = new Lesson(teacher, "Freistunde", null, x, y);
                    timetable.addLesson(lesson);
                }
            }
        }


        return timetable;
    }
}
