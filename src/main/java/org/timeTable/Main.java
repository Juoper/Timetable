package org.timeTable;

import org.apache.commons.text.StringEscapeUtils;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.TimeTableScraper.TimeTableScrapper;
import org.timeTable.models.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    //TODO
    //Change every name to prename + surname

    public LiteSQL liteSQL;

    public static void main(String[] args) throws IOException, InterruptedException {
        LiteSQL.connect();

        TimeTableScrapper timeTableScrapper = new TimeTableScrapper();

        CommunicationLayer communicationLayer = new CommunicationLayer(timeTableScrapper);

        LiteSQL.disconnect();
    }

}
