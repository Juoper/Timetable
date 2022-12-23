package org.timeTable;

import org.apache.commons.text.StringEscapeUtils;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.exceptions.moreThenOneStudentFoundException;
import org.timeTable.CommunicationLayer.exceptions.noStudentFoundException;
import org.timeTable.CommunicationLayer.services.ComServiceDiscord;
import org.timeTable.TimeTableScraper.TimeTableScrapper;
import org.timeTable.models.*;

import java.io.IOException;

public class Main {
    //TODO
    //Change every name to prename + surname

    public LiteSQL liteSQL;

    public static void main(String[] args) throws IOException, InterruptedException {
        LiteSQL.connect();

        TimeTableScrapper timeTableScrapper = new TimeTableScrapper();

        CommunicationLayer communicationLayer = new CommunicationLayer(timeTableScrapper);
        
        ComServiceDiscord discord = new ComServiceDiscord(communicationLayer);
    }

}
