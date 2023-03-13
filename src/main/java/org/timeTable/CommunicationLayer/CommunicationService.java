package org.timeTable.CommunicationLayer;

import org.timeTable.CommunicationLayer.exceptions.subscriptionAlreadyExists;
import org.timeTable.LiteSQL;
import org.timeTable.models.Course;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class CommunicationService {

    //Each Communication Service needs to have its own typeID, communicationID, studentID
    //                                                for example whatsapp has typeID 3
    //                                                communicationID (The id specific to the student that the program then can lookup in the
    //                                                whatsapp table were all the relevant data is saved), for which student the data is saved

    CommunicationLayer communicationLayer;

    public CommunicationService(CommunicationLayer communicationLayer) {
        this.communicationLayer = communicationLayer.registerCommunicationService(this);
    }

    public CommunicationLayer getCommunicationLayer() {
        return communicationLayer;
    }

    protected abstract void sendTimetableNews(int subscription_id, ArrayList<Course> courses);

    protected int subscribeTimetable(int studentId, int updateTime) throws subscriptionAlreadyExists {

        int hour = updateTime / 100;
        int offsetDays = 0;
        if (hour > 10){
            offsetDays = 1;
        }

        try {
            PreparedStatement stmt = LiteSQL.prepareStatement("INSERT INTO subscriptions (student_id, type_id, update_rate, update_time, offsetDays) " +
                    "VALUES (?, 0, 'daily', ?, ?) RETURNING subscription_id");
            stmt.setString(1, String.valueOf(studentId));
            stmt.setString(2, String.valueOf(updateTime));
            stmt.setString(3, String.valueOf(offsetDays));

            ResultSet set = LiteSQL.executeQuery(stmt);


            int subscription_id = set.getInt("subscription_id");
            set.close();

            return subscription_id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void unsubscribeTimetable(int subscription_id, int comServiceID) {

        //Add null statement
        LiteSQL.onUpdate("DELETE FROM subscriptions WHERE subscription_id = " + subscription_id);
        LiteSQL.onUpdate("DELETE FROM comService_" + comServiceID + " WHERE subscription_id = " + subscription_id);

        communicationLayer.stopTimer(subscription_id);

    }

    protected void verifyTimetable(int subscription_id) {
        LiteSQL.onUpdate("UPDATE subscriptions SET verified = 1 WHERE subscription_id = " + subscription_id + " AND verified = 0");
        communicationLayer.newTimer(subscription_id);
    }

    public abstract void stopService();

}
