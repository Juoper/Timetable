package org.timeTable.CommunicationLayer.services;

import java.util.ArrayList;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.CommunicationService;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.subscriptions.Subscription;
import org.timeTable.persistence.subscriptions.SubscriptionRepository;

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

                if (line.toLowerCase().startsWith("stop")) {
                    stopService();
                    loop = false;
                } else if (line.toLowerCase().startsWith("help")) {
                    logger.info("Commands:");
                    logger.info("stop");
                    logger.info("help");
                    logger.info("updatesubscriptions");
                    logger.info("getsubscription");
                    logger.info("sendsubscription <id>");
                } else if (line.toLowerCase().startsWith("updatesubscriptions")) {
                    updateSubscriptions();
                } else if (line.toLowerCase().startsWith("getsubscription")) {
                    subscriptionRepository.findById(Long.valueOf(line.split(" ")[1])).ifPresent(subscription -> {
                        logger.info(subscription.toString());
                    });
                } else if (line.toLowerCase().startsWith("sendsubscription")) {
                    if (line.split(" ").length < 2) {
                        logger.warn("Usage: sendsubscription <id>");
                        return;
                    }
                    var subscription = subscriptionRepository.findById(Long.valueOf(line.split(" ")[1]));
                    if (subscription.isEmpty()) {
                        logger.warn("Subscription not found");
                        return;
                    }
                    communicationLayer.sendTimetableNews(subscription.get());
                }else {
                    logger.info("Unknown command");
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
