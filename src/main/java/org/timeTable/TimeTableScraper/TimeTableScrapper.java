package org.timeTable.TimeTableScraper;

import com.google.gson.*;
import org.timeTable.models.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TimeTableScrapper {

    private ArrayList<Course> courses;
    private ArrayList<Lesson> lessons;
    private WebScraper webScraper;
    private long lastFetch = 0;
    private LocalDate lastRequestDate;

    public TimeTableScrapper() throws InterruptedException, IOException {

        //wrap everything so it pulls the timetable regularly and not only at the creation of the object
        webScraper = new WebScraper();
    }

    private void fetchData(LocalDate date) {


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


        //System.out.println(timeTable);
        JsonElement jelement = JsonParser.parseString(timeTable);

        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("data").getAsJsonObject("result").getAsJsonObject("data");


        JsonArray jarrayCourses = jobject.getAsJsonArray("elements");
        JsonArray jarrayLessons = jobject.getAsJsonObject("elementPeriods").getAsJsonArray("1052");


        //Parse the courses and lessons
        parseCourses(jarrayCourses);
        parseLessons(jarrayLessons, date);
    }

    private void parseLessons(JsonArray jarrayPeriod, LocalDate date) {
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
        gsonBuilder.registerTypeAdapter(CourseResponse.class, new CourseResponseDeserializer());
        gson = gsonBuilder.create();
        CourseResponse courseResponse = gson.fromJson(jarrayElement, CourseResponse.class);
        courses = courseResponse.elements;
    }

    public ArrayList<Lesson> getLessons(){
        fetchData(LocalDate.now());
        return lessons;
    }
    public ArrayList<Course> getCourses(LocalDate date){
        fetchData(date);
        return courses;
    }



}
