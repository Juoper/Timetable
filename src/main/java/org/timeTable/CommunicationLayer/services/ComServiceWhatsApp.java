package org.timeTable.CommunicationLayer.services;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.CommunicationService;
import org.timeTable.Config;
import org.timeTable.LiteSQL;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.subscriptions.Subscription;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static org.timeTable.Main.zoneID;

public class ComServiceWhatsApp extends CommunicationService {
    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);

    //type ID: 0

    public ComServiceWhatsApp(CommunicationLayer communicationLayer) {
        super(communicationLayer);

        System.out.println("WhatsApp online");
    }

    public void sendTimetableNews(Subscription subscription, ArrayList<Course> courses) {
        ResultSet set = LiteSQL.onQuery("SELECT * FROM comService_1 INNER JOIN student ON comService_1.student_id = student.id WHERE subscription_id = " + subscription);
        if (set == null) return;
        String phone_number = "";
        String prename = null;
        String surname = null;

        try {
            phone_number = set.getString("phone_number");
            prename = set.getString("prename");
            surname = set.getString("surname");
            set.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        logger.info("Sending timetable news to " + prename + " " + surname + " with phone number: " + phone_number + " at " + LocalDateTime.now());

        sendTimetableNews(phone_number, prename, surname, courses);
    }

    private void sendTimetableNews(String phone_number, String prename, String surname, ArrayList<Course> courses) {

        StringBuilder builder = new StringBuilder();

        Collections.sort(courses, Comparator.comparing(o -> o.getLessons().get(0).getStartTime()));

        if (Objects.equals(prename, "Q11")) {
            builder = buildTextForQ11(courses);
        } else {

            for (Course course : courses) {
                builder.append("Course: ")
                        .append(course.getName())
                        .append(" | ")
                        .append(course.getShortSubject())
                        .append("\n");
                List<Lesson> lessonList = course.getLessons();
                for (Lesson lesson : lessonList) {
                    builder.append(lesson.getStartTime()).append(" - ").append(lesson.getEndTime()).append(" | ")
                            .append(lesson.getCellstate())
                            .append("\n");
                }
            }
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", Config.whatsAppToken)
                .add("to", "+" + phone_number)
                //TODO add group support
                .add("body", builder.toString())
                .build();

        Request request = new Request.Builder()
                .url("https://api.ultramsg.com/instance41859/messages/chat")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private StringBuilder buildTextForQ11(ArrayList<Course> courses) {
        StringBuilder builder = new StringBuilder();
        Collections.sort(courses, Comparator.comparing(o -> o.getLessons().get(0).getStartTime()));

        builder.append("Diese Kurse fallen heute, am " + ZonedDateTime.now(zoneID).getDayOfWeek() + " aus\n\n");

        for (Course course : courses) {
            if (course.getLessons().stream().anyMatch(l -> l.getCellstate().equals("CANCEL"))) {
                builder.append("Kurs: ")
                        .append(course.getName())
                        .append(" | ")
                        .append(course.getShortSubject())
                        .append(" | ")
                        .append(course.getTeacher())
                        .append("\n");
            }
        }
        builder.append("\nAlle Angaben ohne Gewähr.");
        return builder;

    }

    @Override
    public void stopService() {


    }
}


