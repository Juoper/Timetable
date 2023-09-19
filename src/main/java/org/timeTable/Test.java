package org.timeTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.ZoneId;

@SpringBootApplication
public class Test {
    public static ZoneId zoneID = ZoneId.of( "Europe/Paris");
    private final Logger logger = LoggerFactory.getLogger(org.timeTable.CommunicationLayer.CommunicationLayer.class);
    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(Test.class, args);
    }

    @Autowired
    public Test(extractData extractData) throws IOException {
//        extractData.extractFromPdf("StundenplanQ12.pdf");

    }
}
