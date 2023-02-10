package org.timeTable.CommunicationLayer;

import org.timeTable.CommunicationLayer.exceptions.moreThenOneStudentFoundException;
import org.timeTable.CommunicationLayer.exceptions.noStudentFoundException;
import org.timeTable.CommunicationLayer.services.ComServiceDiscord;
import org.timeTable.LiteSQL;
import org.timeTable.TimeTableScraper.TimeTableScrapper;
import org.timeTable.models.Course;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static org.timeTable.Main.zoneID;

public class CommunicationLayer {
    ArrayList<CommunicationService> comServices;
    ScheduledExecutorService scheduledExecutorService;
    private final TimeTableScrapper timeTableScrapper;

    private final HashMap<Integer, ScheduledFuture<?>> runnableHashMap;

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

    private void pullTimers(){
        ResultSet resultSet = LiteSQL.onQuery("SELECT * FROM subscriptions WHERE verified = 1");

        try {
            while (resultSet.next()) {
                //fix times that are in the past
                int hour = resultSet.getInt("update_time") / 100;
                int minute = resultSet.getInt("update_time") % 100;
                int subscription_id = resultSet.getInt("subscription_id");
                int offsetDays = resultSet.getInt("offsetDays");

                newTimer(subscription_id, hour, minute, offsetDays);

            }
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void newTimer (int subscription_id){
        ResultSet set = LiteSQL.onQuery("SELECT * FROM subscriptions WHERE subscription_id = " + subscription_id);
        try {
            int hour = set.getInt("update_time") / 100;
            int minute = set.getInt("update_time") % 100;
            int offsetDays = set.getInt("offsetDays");

            newTimer(subscription_id, hour, minute, offsetDays);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }
    void newTimer (int subscription_id, int hour, int minute, int offsetDays) {


        ZonedDateTime now = ZonedDateTime.now(zoneID);
        ZonedDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0);



        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        System.out.println("now: " + now + " nextRun: " + nextRun + " inital Delay: " + initialDelay);


        if (initialDelay < 0 ){
            initialDelay = TimeUnit.HOURS.toSeconds(24) + initialDelay;
        }

        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                try {
                    Calendar c = Calendar.getInstance();
                    //if excuted at thursday evening then send the data again at saturday evening
                    if (c.get(Calendar.DAY_OF_WEEK) == 6 - offsetDays){
                        scheduledExecutorService.schedule(this, 3, TimeUnit.DAYS);
                    }else {
                        scheduledExecutorService.schedule(this, 1, TimeUnit.DAYS);
                    }
                    sendTimetableNews(subscription_id);
                } catch (Exception ex) {
                    Thread t = Thread.currentThread();
                    t.getUncaughtExceptionHandler().uncaughtException(t, ex);
                }
            }
        };
        ScheduledFuture<?> schedule = scheduledExecutorService.schedule(runnable, initialDelay, TimeUnit.SECONDS);
        runnableHashMap.put(subscription_id, schedule);

    }
    //Student which student to get the courses for

    //public unsubscribeTimtableNews (Student student)

    public void sendTimetableNews (int subscription_id) {
        ResultSet set = LiteSQL.onQuery("SELECT * FROM subscriptions WHERE subscription_id = " + subscription_id);
        if (set == null) return;

        int student_id = 0;
        int type_id = -1;
        int offsetDays = -1;

        try {
            student_id = set.getInt("student_id");
            type_id = set.getInt("type_id");
            offsetDays = set.getInt("offsetDays");
            set.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ZonedDateTime lDate = ZonedDateTime.now(zoneID).plusDays(offsetDays);
        
        ArrayList<Course> courses = timeTableScrapper.getCourses(lDate);
        ResultSet coursesSet = LiteSQL.onQuery("SELECT * FROM student_course WHERE student_id = " + student_id);
        ArrayList<Course> studentCourses = new ArrayList<>();
        try {
            while (coursesSet.next()) {
                int course_id = coursesSet.getInt("course_id");
                if (courses.stream().anyMatch(c -> c.getId() == course_id)){
                    Course course = courses.stream().filter(c -> c.getId() == course_id).findFirst().get();

                    if (!course.getLessons().isEmpty()){
                        studentCourses.add(course);
                    }
                }

            }
            coursesSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        

        CommunicationService comService = null;
        switch (type_id){
            case 0:
                //Discord
                comService = comServices.stream().filter(communicationService -> communicationService instanceof ComServiceDiscord).findFirst().get();
                comService.sendTimetableNews(subscription_id, studentCourses);
                break;
            default:
                break;
        }

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
        int id = 0;
        try {
            if(set != null){
                if (set.next()){
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
        return id;
    }

    protected void stopTimer(int subscriptionId) {
        ScheduledFuture<?> scheduledFuture = runnableHashMap.get(subscriptionId);
        try {
            scheduledFuture.cancel(false);

        }catch (NullPointerException ignored){}
    }

}


