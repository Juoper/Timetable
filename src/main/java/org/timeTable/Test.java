package org.timeTable;

import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.CommunicationService;
import org.timeTable.CommunicationLayer.services.ComServiceDiscord;
import org.timeTable.TimeTableScraper.TimeTableScrapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        LiteSQL.connect();
        ResultSet set = LiteSQL.onQuery("SELECT id FROM course");

        for (int i = 2; i < 127; i++) {

            System.out.println(i);
            LiteSQL.onUpdate("Insert Into student_course (student_id, course_id) Values (0, " + i + ")");
        }

    }

}
