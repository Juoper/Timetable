package org.timeTable;

import org.timeTable.models.Lesson;
import org.timeTable.models.Year;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
        Year year = new Year();
        Lesson lesson = new Lesson("1k1", 5, 8);
        Lesson lesson2 = new Lesson("1k1", 5, 8);

        year.addLesson(lesson);
        year.addLesson(lesson);

    }

}
