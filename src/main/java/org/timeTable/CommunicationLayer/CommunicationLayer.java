package org.timeTable.CommunicationLayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.CommunicationLayer.exceptions.moreThenOneStudentFoundException;
import org.timeTable.CommunicationLayer.exceptions.noStudentFoundException;
import org.timeTable.CommunicationLayer.services.ComServiceDiscord;
import org.timeTable.CommunicationLayer.services.ComServiceWhatsApp;
import org.timeTable.LiteSQL;
import org.timeTable.TimeTableScraper.TimeTableScrapper;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.subscriptions.Subscription;
import org.timeTable.persistence.subscriptions.SubscriptionRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static org.timeTable.Main.zoneID;

@Service
public class CommunicationLayer {
    ArrayList<CommunicationService> comServices;
    ScheduledExecutorService scheduledExecutorService;
    @Autowired
    SubscriptionRepository subscriptionRepository;
    private final TimeTableScrapper timeTableScrapper;
    private final HashMap<Long, ScheduledFuture<?>> runnableHashMap;
    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);

    public CommunicationLayer(TimeTableScrapper timeTableScrapper) {
        this.timeTableScrapper = timeTableScrapper;
        comServices = new ArrayList<>();
        runnableHashMap = new HashMap<>();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        pullTimers();
        //sendTimetableNews(12);
    }


    public CommunicationLayer registerCommunicationService(CommunicationService service) {
        comServices.add(service);
        return this;
    }

    private void pullTimers() {
        List<Subscription> subscriptions = subscriptionRepository.findAllByVerified();
        subscriptions.forEach(this::newTimer);

    }

    void newTimer(Subscription subscription) {

        ZonedDateTime now = ZonedDateTime.now(zoneID);
        ZonedDateTime nextRun = ZonedDateTime.from(subscription.updateTime);

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        logger.info("New Timer created for subscription_id: " + subscription.getId() + " and sending time for " + subscription.updateTime + " with offset of " + subscription.offsetDays + " days");

        if (initialDelay < 0) {
            initialDelay = TimeUnit.HOURS.toSeconds(24) + initialDelay;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    Calendar c = Calendar.getInstance();
                    //if excuted at thursday evening then send the data again at saturday evening
                    if (c.get(Calendar.DAY_OF_WEEK) == 6 - subscription.offsetDays) {
                        scheduledExecutorService.schedule(this, 3, TimeUnit.DAYS);
                    } else {
                        scheduledExecutorService.schedule(this, 1, TimeUnit.DAYS);
                    }
                    sendTimetableNews(subscription);
                } catch (Exception ex) {
                    Thread t = Thread.currentThread();
                    t.getUncaughtExceptionHandler().uncaughtException(t, ex);
                }
            }
        };
        ScheduledFuture<?> schedule = scheduledExecutorService.schedule(runnable, initialDelay, TimeUnit.SECONDS);
        runnableHashMap.put(subscription.getId(), schedule);

    }
    //Student which student to get the courses for

    //public unsubscribeTimtableNews (Student student)

    public void sendTimetableNews(Subscription subscription) {
        logger.info("Sending timetable news for subscription_id: " + subscription.getId() + " student_id: " + subscription.student.getId() + " with offset of " + subscription.offsetDays + " days");

        ArrayList<Course> studentCourses = getCourseDataOfStudent(subscription.student, subscription.offsetDays);

        CommunicationService comService = null;
        switch (subscription.getTypeId()) {
            case 0:
                //Discord
                comService = comServices.stream().filter(communicationService -> communicationService instanceof ComServiceDiscord).findFirst().get();
                comService.sendTimetableNews(subscription, studentCourses);
                break;
            case 1:
                //WhatsApp
                comService = comServices.stream().filter(communicationService -> communicationService instanceof ComServiceWhatsApp).findFirst().get();
                comService.sendTimetableNews(subscription_id, studentCourses);


            default:
                break;
        }
    }

    public ArrayList<Course> getCourseDataOfStudent(Student student, int offsetDays) {
        ZonedDateTime lDate = ZonedDateTime.now(zoneID).plusDays(offsetDays);

        ArrayList<Course> allCourses = timeTableScrapper.getCourses(lDate);
        List<Course> studentCourses = student.courses;

        allCourses.forEach(course -> {
            if (studentCourses.stream().noneMatch(c -> c.getId() == course.getId())){
                allCourses.remove(course);
            }
        });

        return allCourses;
    }

    public int getStudentIdByName(String prename, String surname) throws noStudentFoundException, moreThenOneStudentFoundException {

        boolean prenameGiven = !prename.equals("");
        boolean surnameGiven = !surname.equals("");

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT id FROM student WHERE ");

        int namesGiven = -1;
        if (surnameGiven && prenameGiven) {
            builder.append("prename = ? AND surname = ?");
            namesGiven = 1;
        } else if (prenameGiven) {
            builder.append("prename = ?");
            namesGiven = 0;
        } else if (surnameGiven) {
            builder.append("surname = ?");
            namesGiven = 2;
        }

        PreparedStatement stmt = LiteSQL.prepareStatement(builder.toString());

        try {
            switch (namesGiven) {
                case 0 -> {
                    stmt.setString(1, prename);
                    break;
                }
                case 1 -> {
                    stmt.setString(1, prename);
                    stmt.setString(2, surname);
                    break;
                }
                case 2 -> stmt.setString(1, surname);
                default -> throw new noStudentFoundException("No names given");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        ResultSet set = LiteSQL.executeQuery(stmt);
        int id = -1;
        try {
            if (set != null) {
                if (set.next()) {
                    id = set.getInt("id");
                    if (set.next()) {
                        set.close();
                        throw new moreThenOneStudentFoundException("Found more then one Student");
                    }
                }
            } else {
                throw new noStudentFoundException("Can't find a student matching the given data");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (id <= 0) throw new noStudentFoundException("Can't find a student matching the given data");

        return id;
    }

    protected void stopTimer(long subscriptionId) {

        ScheduledFuture<?> scheduledFuture = runnableHashMap.get(subscriptionId);
        try {
            scheduledFuture.cancel(false);
            logger.info("Timer for subscription_id: " + subscriptionId + " stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


