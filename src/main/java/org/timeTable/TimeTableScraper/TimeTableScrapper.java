package org.timeTable.TimeTableScraper;

import com.google.gson.*;
import org.springframework.cglib.core.Local;
import org.timeTable.models.*;
import org.timeTable.models.json.Element;
import org.timeTable.models.json.ElementPeriod;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

public class TimeTableScrapper {

    private ArrayList<Course> courses;

    private ArrayList<Lesson> lessons;
    public TimeTableScrapper() throws IOException, InterruptedException {

        //wrap everything so it pulls the timetable regularly
        WebScraper wS = new WebScraper();
        LocalDate date = LocalDate.now();
        String timeTable = wS.getTimetable(date);
        

        //System.out.println(timeTable);
        JsonElement jelement = JsonParser.parseString(timeTable);

        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("data").getAsJsonObject("result").getAsJsonObject("data");


        JsonArray jarrayCourses = jobject.getAsJsonArray("elements");
        JsonArray jarrayLessons = jobject.getAsJsonObject("elementPeriods").getAsJsonArray("1052");


        //Parse the courses and lessons
        parseCourses(jarrayCourses);
        parseLessons(jarrayLessons);
        
    }

    private void parseLessons(JsonArray jarrayPeriod){
        Gson gson = new Gson();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LessonResponse.class, new LessonResponseDeserializer(courses));
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
        return lessons;
    }
    public ArrayList<Course> getCourses(){
        return courses;
    }
}
