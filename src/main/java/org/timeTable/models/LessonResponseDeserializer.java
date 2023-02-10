package org.timeTable.models;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.timeTable.models.Lesson.getLessonHour;

public class LessonResponseDeserializer implements JsonDeserializer<LessonResponse> {

    private final ArrayList<Course> courses;
    private final ZonedDateTime date;

    public LessonResponseDeserializer(ArrayList<Course> courses, ZonedDateTime date) {
        this.courses = courses;
        this.date = date;
    }

    @Override
    public LessonResponse deserialize(JsonElement json, Type type,
                                      JsonDeserializationContext context) throws JsonParseException {

        JsonArray jArray = (JsonArray) json;

        LessonResponse lessonResponse = new LessonResponse();
        String localDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (int i = 1; i < jArray.size(); i++) {
            JsonObject jObject = (JsonObject) jArray.get(i);
            int id = jObject.get("elements").getAsJsonArray().get(jObject.get("elements").getAsJsonArray().size() - 1).getAsJsonObject().get("id").getAsInt();
            String date = jObject.get("date").getAsString();
            int startTime = jObject.get("startTime").getAsInt();
            int endTime = jObject.get("endTime").getAsInt();

            int hour = getLessonHour(startTime);

            String cellstate = jObject.get("cellState").getAsString();

            if (date.equals(localDate)) {
                Course course = courses.stream().filter(c -> c.getUntisId() == id).findFirst().get();
                Lesson lesson = new Lesson(course, Integer.parseInt(date), hour, cellstate, startTime, endTime);
                course.lessons.add(lesson);
                lessonResponse.elements.add(lesson);
            }
        }

        return lessonResponse;
    }
}
