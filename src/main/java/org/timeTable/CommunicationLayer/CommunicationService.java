package org.timeTable.CommunicationLayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.CommunicationLayer.exceptions.subscriptionAlreadyExists;
import org.timeTable.LiteSQL;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.subscriptions.Subscription;
import org.timeTable.persistence.subscriptions.SubscriptionRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public abstract class CommunicationService {

    private SubscriptionRepository subscriptionRepository;

    //Each Communication Service needs to have its own typeID, communicationID, studentID
    //                                                for example whatsapp has typeID 3
    //                                                communicationID (The id specific to the student that the program then can lookup in the
    //                                                whatsapp table were all the relevant data is saved), for which student the data is saved

    CommunicationLayer communicationLayer;

    public CommunicationService(CommunicationLayer communicationLayer) {
        this.communicationLayer = communicationLayer.registerCommunicationService(this);
    }

    @Autowired
    public CommunicationService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public CommunicationLayer getCommunicationLayer() {
        return communicationLayer;
    }

    protected abstract void sendTimetableNews(Subscription subscription, ArrayList<Course> courses);

    protected void unsubscribeTimetable(Subscription subscription, int comServiceID) {

        //Add null statement
        LiteSQL.onUpdate("DELETE FROM subscriptions WHERE subscription_id = " + subscription);
        LiteSQL.onUpdate("DELETE FROM comService_" + comServiceID + " WHERE subscription_id = " + subscription);

        communicationLayer.stopTimer(subscription.getId());

    }

    protected void verifyTimetable(long subscription_id) {
        Optional<Subscription> subscription = subscriptionRepository.findById(subscription_id);
        subscription.ifPresent(value -> communicationLayer.newTimer(value));
    }

    public abstract void stopService();

}
