package org.timeTable.communicationLayer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.CommunicationService;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.subscriptions.Subscription;
import org.timeTable.persistence.subscriptions.SubscriptionRepository;

import java.util.ArrayList;
import java.util.Scanner;

@Service
public class ComServiceCommandLine extends CommunicationService {

    private final Logger logger = LoggerFactory.getLogger(ComServiceCommandLine.class);
    private final CommunicationLayer communicationLayer;

    private boolean loop = true;

    @Autowired
    public ComServiceCommandLine(SubscriptionRepository subscriptionRepository, CommunicationLayer communicationLayer) {
        super(subscriptionRepository);

        this.communicationLayer = communicationLayer;
        communicationLayer.registerCommunicationService(this);

        logger.info("ComServiceCommandLine started");

        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            // Reading data using readLine

            while (loop) {

                String line = scanner.nextLine();
                switch (line.toLowerCase()) {
                    case "stop" -> {
                        stopService();
                        loop = false;
                    }
                    case "help" -> {
                        logger.info("Commands:");
                        logger.info("stop");
                        logger.info("help");
                        logger.info("updatesubscriptions");
                    }
                    case "updatesubscriptions" -> updateSubscriptions();
                    default -> logger.info("Unknown command");
                }
            }
        });
        thread.start();

    }

    private void updateSubscriptions() {
        logger.info("updating");
        communicationLayer.startTimers();
    }

    @Override
    protected void sendTimetableNews(Subscription subscription, ArrayList<Course> courses) {
        //Not needed because its the command line
    }

    @Override
    public void stopService() {
        loop = false;
    }
}
