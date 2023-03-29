package org.timeTable;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.services.ComServiceDiscord;
import org.timeTable.CommunicationLayer.services.ComServiceWhatsApp;
import org.timeTable.TimeTableScraper.TimeTableScrapper;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class Main {
    //TODO
    //Change every name to prename + surname

    public LiteSQL liteSQL;
    public static ZoneId zoneID = ZoneId.of( "Europe/Paris");

    public static void main(String[] args) throws IOException, InterruptedException {
        Config.loadConfig();
        LiteSQL.connect();
        TimeTableScrapper timeTableScrapper = new TimeTableScrapper();

        CommunicationLayer communicationLayer = new CommunicationLayer(timeTableScrapper);
        
        ComServiceDiscord discord = new ComServiceDiscord(communicationLayer);
        ComServiceWhatsApp whatsApp = new ComServiceWhatsApp(communicationLayer);
    }

}
