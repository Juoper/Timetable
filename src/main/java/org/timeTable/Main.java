package org.timeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.services.ComServiceDiscord;
import org.timeTable.CommunicationLayer.services.ComServiceWhatsApp;
import org.timeTable.TimeTableScraper.TimeTableScrapper;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;


@SpringBootApplication
public class Main {
    //TODO
    //Change every name to prename + surname
    public static ZoneId zoneID = ZoneId.of( "Europe/Paris");
    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        SpringApplication.run(Main.class, args);
    }

    @Autowired
    public Main(CommunicationLayer communicationLayer, extractData extractData) throws IOException, InterruptedException {
        Config.loadConfig();
        extractData.extractFromPdf("Stundenplan.pdf");

        communicationLayer.startTimers();
    }
}
