package org.timeTable.persistence.lesson;

import com.google.gson.*;
import org.timeTable.persistence.course.Course;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.timeTable.persistence.lesson.Lesson.getLessonHour;

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
//TODO check if exisits otherwise print out no course found
        JsonArray jArray = (JsonArray) json;

        LessonResponse lessonResponse = new LessonResponse();
        String localDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (int i = 1; i < jArray.size(); i++) {
            JsonObject jObject = (JsonObject) jArray.get(i);
            int id = jObject.get("elements").getAsJsonArray().get(jObject.get("elements").getAsJsonArray().size() - 1).getAsJsonObject().get("id").getAsInt();
            String date = jObject.get("date").getAsString();
            LocalTime startTime = LocalTime.parse(jObject.get("startTime").getAsString());
            LocalTime endTime = LocalTime.parse(jObject.get("endTime").getAsString());

            String cellstate = jObject.get("cellState").getAsString();

            if (date.equals(localDate)) {
                Course course = courses.stream().filter(c -> c.getUntisId() == id).findFirst().get();
                Lesson lesson = new Lesson(course, this.date.getDayOfWeek(), startTime, endTime, cellstate);
                course.lessons.add(lesson);
                lessonResponse.elements.add(lesson);
            }
        }

        return lessonResponse;
    }
}
