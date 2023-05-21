package org.timeTable.communicationLayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.communicationLayer.exceptions.MoreThanOneStudentFoundException;
import org.timeTable.communicationLayer.exceptions.NoStudentFoundException;
import org.timeTable.communicationLayer.services.ComServiceDiscord;
import org.timeTable.communicationLayer.services.ComServiceWhatsApp;
import org.timeTable.TimeTableScraper.TimeTableScrapper;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.student.StudentRepository;
import org.timeTable.persistence.subscriptions.Subscription;
import org.timeTable.persistence.subscriptions.SubscriptionRepository;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static org.timeTable.Main.zoneID;

@Service
public class CommunicationLayer {
    ScheduledExecutorService scheduledExecutorService;
    ArrayList<CommunicationService> comServices;

    private final SubscriptionRepository subscriptionRepository;
    private final TimeTableScrapper timeTableScrapper;
    private final HashMap<Long, ScheduledFuture<?>> runnableHashMap;
    private final HashMap<Long, Integer> subscriptionsHashMap;
    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);
    private final StudentRepository studentRepository;

    @Autowired
    public CommunicationLayer(SubscriptionRepository subscriptionRepository, TimeTableScrapper timeTableScrapper, StudentRepository studentRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.timeTableScrapper = timeTableScrapper;
        this.studentRepository = studentRepository;
        comServices = new ArrayList<>();

        runnableHashMap = new HashMap<>();
        subscriptionsHashMap = new HashMap<>();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    public void startTimers() {
        pullTimers();
    }

    private void pullTimers() {
        logger.info("Pulling timers from database");
        List<Subscription> subscriptions = subscriptionRepository.findAllByVerifiedTrue();
        subscriptions.forEach(this::newTimer);

    }

    void newTimer(Subscription subscription) {

        if (subscriptionsHashMap.containsValue(subscription.hashCode())) {
            logger.info("Timer already exists for subscription_id: " + subscription.getId());
            return;
        } else {
            stopTimer(subscription.getId());
            subscriptionsHashMap.remove(subscription.getId());
            runnableHashMap.remove(subscription.getId());
        }



        ZonedDateTime now = ZonedDateTime.now(zoneID);
        ZonedDateTime nextRun = ZonedDateTime.of(subscription.getUpdateTime().atDate(LocalDate.now()), ZoneId.of("ECT", ZoneId.SHORT_IDS));

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        logger.info("New Timer created for subscription_id: " + subscription.getId() + " and sending time for " + subscription.getUpdateTime() + " with offset of " + subscription.getOffsetDays() + " days");

        if (initialDelay < 0) {
            initialDelay = TimeUnit.HOURS.toSeconds(24) + initialDelay;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    Calendar c = Calendar.getInstance();
                    //if excuted at thursday evening then send the data again at saturday evening
                    if (c.get(Calendar.DAY_OF_WEEK) == 6 - subscription.getOffsetDays()) {
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
        subscriptionsHashMap.put(subscription.getId(), subscription.hashCode());

    }
    //Student which student to get the courses for

    //public unsubscribeTimtableNews (Student student)

    public CommunicationLayer registerCommunicationService(CommunicationService service) {
        comServices.add(service);
        return this;
    }

    public void sendTimetableNews(Subscription subscription) {
        logger.info("Sending timetable news for subscription_id: " + subscription.getId() + " student_id: " + subscription.student.getId() + " with offset of " + subscription.getOffsetDays() + " days");

        ArrayList<Course> studentCourses = getCourseDataOfStudent(subscription.student, subscription.getOffsetDays());

        CommunicationService comService = null;

        switch (subscription.getClass().getSimpleName()) {
            case "ComServiceDiscordSubscription":
                //Discord
                comService = comServices.stream().filter(communicationService -> communicationService instanceof ComServiceDiscord).findFirst().get();
                comService.sendTimetableNews(subscription, studentCourses);
                break;
            case "ComServiceWhatsAppSubscription":
                //WhatsApp
                comService = comServices.stream().filter(communicationService -> communicationService instanceof ComServiceWhatsApp).findFirst().get();

                comService.sendTimetableNews(subscription, studentCourses);
                break;
            default:
                break;
        }
    }

    public ArrayList<Course> getCourseDataOfStudent(Student student, int offsetDays) {
        ZonedDateTime lDate = ZonedDateTime.now(zoneID).plusDays(offsetDays);

        ArrayList<Course> allCourses = timeTableScrapper.getCourses(lDate);
        ArrayList<Lesson> allLessons = timeTableScrapper.getLessons(lDate);
        List<Course> studentCourses = student.courses.stream().toList();

        ArrayList<Course> filteredCourses = new ArrayList<Course>();

        allCourses.forEach(course -> {
            if (studentCourses.stream().anyMatch(c -> c.getId() == course.getId())) {
                if (course.getLessons().size() > 0) {
                    filteredCourses.add(course);
                }
            }
        });

        return filteredCourses;
    }

    public Student getStudentByName(String prename, String surname) throws NoStudentFoundException, MoreThanOneStudentFoundException {
        List<Student> students = studentRepository.findAllByPrenameOrAndSurname(prename, surname);

        Student student;
        if (students.size() > 0) {
            if (students.size() > 1) {
                throw new MoreThanOneStudentFoundException("Found more then one Student");
            }
            student = students.get(0);

        } else {
            throw new NoStudentFoundException("Can't find a student matching the given data");
        }

        return student;
    }

    protected void stopTimer(long subscriptionId) throws NullPointerException {

        ScheduledFuture<?> scheduledFuture = runnableHashMap.get(subscriptionId);
        try {
            scheduledFuture.cancel(false);
            logger.info("Timer for subscription_id: " + subscriptionId + " stopped");
        } catch (NullPointerException ignored) {
        }
    }


}


