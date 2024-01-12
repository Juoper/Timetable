package org.timeTable.CommunicationLayer.services;

import static org.timeTable.Main.zoneID;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.CommunicationLayer.CommunicationService;
import org.timeTable.communicationLayer.exceptions.NoCanceledCoursesException;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.subscriptions.Subscription;
import org.timeTable.persistence.subscriptions.SubscriptionRepository;
import org.timeTable.persistence.subscriptions.comServiceWhatsApp.ComServiceWhatsAppSubscription;

@Service
public class ComServiceWhatsApp extends CommunicationService {
    private final Logger logger = LoggerFactory.getLogger(ComServiceWhatsApp.class);
    private final CommunicationLayer communicationLayer;

    public ComServiceWhatsApp(
        SubscriptionRepository subscriptionRepository,
        CommunicationLayer communicationLayer
    ) {
        super(subscriptionRepository, communicationLayer);
        this.communicationLayer = communicationLayer;
        communicationLayer.registerCommunicationService(this);

        logger.info("WhatsApp online");
    }

    public void sendTimetableNews(
        Subscription subscription,
        ArrayList<Course> courses
    ) {

        if (subscription instanceof ComServiceWhatsAppSubscription comServiceWhatsAppSubscription) {
            sendTimetableNews(comServiceWhatsAppSubscription.getPhone_number(), comServiceWhatsAppSubscription.getStudent().getPrename(), courses);
            logger.info(
                "Sending timetable news to " + subscription.student.getPrename() + " " + subscription.student.getSurname() + " with phone number: " + comServiceWhatsAppSubscription.getPhone_number() +
                " at " + LocalDateTime.now());
        }
    }

    private void sendTimetableNews(
        String phoneNumber,
        String prename,
        ArrayList<Course> courses
    ) {

        StringBuilder builder = new StringBuilder();

        courses.sort(Comparator.comparing(o -> o.getLessons().iterator().next().getStartTime()));

        if (Objects.equals(prename, "Q12")) {
            try {
                builder = buildTextForQ11(courses);
            } catch (NoCanceledCoursesException e) {
                logger.info("No canceled courses found at {} ", LocalDateTime.now());
                return;
            }
        } else {

            for (Course course : courses) {
                builder.append("Course: ")
                       .append(course.getName())
                       .append(" | ")
                       .append(course.getShortSubject())
                       .append("\n");
                List<Lesson> lessonList = course.getLessons().stream().toList();
                for (Lesson lesson : lessonList) {
                    builder.append(lesson.getStartTime()).append(" - ").append(lesson.getEndTime()).append(" | ")
                           .append(lesson.getCellstate())
                           .append("\n");
                }
            }
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                               .add("chatId", phoneNumber)
                               .add("text", builder.toString())
                               .add("session", "default")
                               .build();

        Request request = new Request.Builder()
                              .url("http://whatsapp:3000/api/sendText")
                              .post(body)
                              .addHeader("content-type", "application/json")
                              .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                logger.info("Response wasn't successfull: " + response);
            }
            response.close();
        } catch (IOException e) {
            logger.error(e.toString());
            logger.error("Error while sending timetable news to " + prename + " " + phoneNumber + " at " + LocalDateTime.now());
        }

    }

    private StringBuilder buildTextForQ11(ArrayList<Course> courses) throws NoCanceledCoursesException {
        StringBuilder builder = new StringBuilder();

        if (courses.stream().filter(course -> course.getLessons().stream().anyMatch(l -> l.getCellstate().equals("CANCEL"))).toList().isEmpty()) {

            return builder.append("Heute fallen keine Kurse aus!");
        }

        logger.info("Building text for Q11 at {} ", courses.size());

        Collections.sort(courses, Comparator.comparing(o -> o.getLessons().iterator().next().getStartTime()));

        ZonedDateTime zdtNow = ZonedDateTime.now(zoneID);
        String stringDate = new SimpleDateFormat("dd.MM.yyyy").format(Date.from(zdtNow.toInstant()));

        builder.append("Diese Kurse fallen heute am " + zdtNow.getDayOfWeek() + ", den " + stringDate + ", aus:\n\n");

        for (Course course : courses) {
            if (course.getLessons().stream().anyMatch(l -> l.getCellstate().equals("CANCEL"))) {

                String lessons = course.getLessons().stream()
                                       .filter(lesson -> lesson.getCellstate().equals("CANCEL"))
                                       .sorted()
                                       .map(l -> Lesson.getLessonHour(l.getStartTime())).toList().toString();
                lessons = lessons
                              .replace("[", "")
                              .replace("]", "")
                              .replace(", ", "/");

                builder.append("Kurs ")
                       .append(course.getName())
                       .append(" | ")
                       .append(course.getShortSubject())
                       .append(" | ")
                       .append(course.getTeacher().getAbbreviation())
                       .append(" | ")
                       .append(lessons)
                       .append("\n");
            }
        }
        builder.append("\n(_Alle Angaben ohne Gewähr_)");
        return builder;

    }

    @Override
    public void stopService() {

    }
}


