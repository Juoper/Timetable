package org.timeTable.TimeTableScraper;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.course.CourseResponse;
import org.timeTable.persistence.course.CourseResponseDeserializer;
import org.timeTable.persistence.lesson.Lesson;
import org.timeTable.persistence.lesson.LessonResponse;
import org.timeTable.persistence.lesson.LessonResponseDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.timeTable.Main.zoneID;

@Service
public class TimeTableScrapper {

    private ArrayList<Course> courses;
    private ArrayList<Lesson> lessons;
    private WebScraper webScraper;

    private final CourseResponseDeserializer courseResponseDeserializer;
    private long lastFetch = 0;
    private LocalDate lastRequestDate;

    @Autowired
    public TimeTableScrapper(CourseResponseDeserializer courseResponseDeserializer) throws InterruptedException, IOException {
        this.courseResponseDeserializer = courseResponseDeserializer;

        //wrap everything so it pulls the timetable regularly and not only at the creation of the object
        webScraper = new WebScraper();
    }

    private void fetchData(ZonedDateTime date) {


        if (lastRequestDate != null && lastRequestDate.equals(date)){
            if (System.currentTimeMillis() <= lastFetch + TimeUnit.MINUTES.toMillis(30)){
                return;
            }
        }

        lastFetch = System.currentTimeMillis();

        String timeTable = null;
        try {
            timeTable = webScraper.getTimetable(date);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        JsonElement jelement = JsonParser.parseString(timeTable);

        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("data").getAsJsonObject("result").getAsJsonObject("data");


        JsonArray jarrayCourses = jobject.getAsJsonArray("elements");
        JsonArray jarrayLessons = jobject.getAsJsonObject("elementPeriods").getAsJsonArray("1052");


        //Parse the courses and lessons
        parseCourses(jarrayCourses);
        parseLessons(jarrayLessons, date);
    }

    private void parseLessons(JsonArray jarrayPeriod, ZonedDateTime date) {
        Gson gson = new Gson();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LessonResponse.class, new LessonResponseDeserializer(courses, date));
        gson = gsonBuilder.create();
        LessonResponse lessonResponse = gson.fromJson(jarrayPeriod, LessonResponse.class);

        lessons = lessonResponse.elements;
    }

    private void parseCourses(JsonArray jarrayElement){
        Gson gson = new Gson();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(CourseResponse.class, courseResponseDeserializer);
        gson = gsonBuilder.create();
        CourseResponse courseResponse = gson.fromJson(jarrayElement, CourseResponse.class);
        courses = courseResponse.elements;
    }

    public ArrayList<Lesson> getLessons(ZonedDateTime date){
        fetchData(date);
        return lessons;
    }
    public ArrayList<Course> getCourses(ZonedDateTime date){
        fetchData(date);
        return courses;
    }



}
