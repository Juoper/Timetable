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
        
        year.getStudents().forEach(student -> {
            LiteSQL.onUpdate("INSERT INTO student (prename, surname) VALUES ('" + student.getPrename() + "', '" + student.getSurname() + "')");

            student.getTimetable().getCourses().forEach(course -> {
                LiteSQL.onUpdate("INSERT INTO course (name, subject) VALUES ('" + course.getName() + "', '" + course.getSubject() + "')");

                course.getLessons().forEach(lesson -> {
                    LiteSQL.onUpdate("INSERT INTO lesson (day, hour) VALUES ('" + lesson.getDay() + "', '" + lesson.getHour() + "')");
                });
            });

        });




        LiteSQL.disconnect();

//        ManagementOverAll moa = new ManagementOverAll();
//        moa.AlleSchülerAusgeben();
//        System.out.println("-----");
//        Kurs k = moa.getGrundkursVon("Benedikt Gwuzdz");
//        moa.KurslisteVon(k);
//        System.out.println("-----");
//        moa.KurslisteVon(new Kurs("Mu", "1mu1", "DER"));
    }

}
