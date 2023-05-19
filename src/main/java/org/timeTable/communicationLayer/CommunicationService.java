package org.timeTable.communicationLayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.subscriptions.Subscription;
import org.timeTable.persistence.subscriptions.SubscriptionRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public abstract class CommunicationService {

    private SubscriptionRepository subscriptionRepository;

    private CommunicationLayer communicationLayer;

    @Autowired
    protected CommunicationService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }
    @Autowired
    public CommunicationService(SubscriptionRepository subscriptionRepository, CommunicationLayer communicationLayer) {
        this.subscriptionRepository = subscriptionRepository;
        this.communicationLayer = communicationLayer.registerCommunicationService(this);
    }

    public CommunicationLayer getCommunicationLayer() {
        return this.communicationLayer;
    }

    protected abstract void sendTimetableNews(Subscription subscription, ArrayList<Course> courses);

    protected void unsubscribeTimetable(Subscription subscription) {

        //Add null statement
        subscriptionRepository.delete(subscription);

        communicationLayer.stopTimer(subscription.getId());

    }

    protected void verifyTimetable(long subscription_id) {
        Optional<Subscription> subscription = subscriptionRepository.findById(subscription_id);
        subscription.ifPresent(value -> communicationLayer.newTimer(value));
    }

    public abstract void stopService();

}
