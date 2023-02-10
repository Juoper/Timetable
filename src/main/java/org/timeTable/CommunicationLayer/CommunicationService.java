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

    protected int subscribeTimetable(int student_id, Long userID, Long channelID, String channel_type, int updateTime, int comServiceID) throws subscriptionAlreadyExists {

        int hour = updateTime / 100;
        int offsetDays = 0;
        if (hour > 10){
            offsetDays = 1;
        }

        try {

            ResultSet set = LiteSQL.onQuery("SELECT * FROM comService_" + comServiceID + " WHERE student_id = " + student_id + " AND channel_id = " + channelID);

            if (set.next()) {
                set.close();
                throw new subscriptionAlreadyExists("You are already subscribed to this timetable");
            }

            //Add null statement


            PreparedStatement stmt = LiteSQL.prepareStatement("INSERT INTO subscriptions (student_id, type_id, update_rate, update_time, offsetDays) " +
                    "VALUES (?, 0, 'daily', ?, ?) RETURNING subscription_id");

            stmt.setString(1, String.valueOf(student_id));
            stmt.setString(2, String.valueOf(updateTime));
            stmt.setString(3, String.valueOf(offsetDays));

            set = LiteSQL.executeQuery(stmt);

            //set = LiteSQL.onQuery("INSERT INTO subscriptions (student_id, type_id, update_rate, update_time, offsetDays) " +
            //        "VALUES (" + student_id + ", 0, 'daily', " + updateTime + ", 1) RETURNING subscription_id");

            int subscription_id = set.getInt("subscription_id");

            set.close();
            //move to ComServiceDiscord
            LiteSQL.onUpdate("INSERT INTO comService_" + comServiceID + " (subscription_id, student_id, user_id, channel_id, channel_type) " +
                    "VALUES (" + subscription_id + ", " + student_id + ", " + userID + ", " + channelID + ", '" + channel_type + "')");

            //communicationLayer.newTimer(subscription_id, hour, minute, 1);
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
