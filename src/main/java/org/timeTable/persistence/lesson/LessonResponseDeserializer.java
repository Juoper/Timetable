package org.timeTable.persistence.lesson;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.persistence.course.Course;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class LessonResponseDeserializer implements JsonDeserializer<LessonResponse> {
    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);

    private final ArrayList<Course> courses;
    private final ZonedDateTime date;

    public LessonResponseDeserializer(ArrayList<Course> courses, ZonedDateTime date) {
        this.courses = courses;
        this.date = date;
    }

    @Override
    public LessonResponse deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
//TODO check if exisits otherwise print out no course found
        JsonArray jArray = (JsonArray) json;

        LessonResponse lessonResponse = new LessonResponse();
        String localDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (int i = 1; i < jArray.size(); i++) {
            JsonObject jObject = (JsonObject) jArray.get(i);
            int id = jObject.get("elements").getAsJsonArray().get(jObject.get("elements").getAsJsonArray().size() - 1).getAsJsonObject().get("id").getAsInt();
            String date = jObject.get("date").getAsString();
            String startTimePattern = "HHmm";
            if (jObject.get("startTime").getAsString().length() == 3) {
                 startTimePattern = "Hmm";
            }
            String endTimePattern = "HHmm";
            if (jObject.get("endTime").getAsString().length() == 3) {
                endTimePattern = "Hmm";
            }

            LocalTime startTime = LocalTime.parse(jObject.get("startTime").getAsString(), DateTimeFormatter.ofPattern(startTimePattern));
            LocalTime endTime = LocalTime.parse(jObject.get("endTime").getAsString(), DateTimeFormatter.ofPattern(endTimePattern));

            String cellState = jObject.get("cellState").getAsString();

            if (date.equals(localDate)) {

                Optional<Course> course = courses.stream().filter(c -> c.getUntisId() == id).findFirst();

                if (course.isEmpty()) {
                    logger.warn("Found lesson without matching database entry: " + id);
                } else {
                    Lesson lesson = new Lesson(course.get(), this.date.getDayOfWeek(), startTime, endTime, cellState);
                    course.get().addLesson(lesson);
                    lessonResponse.elements.add(lesson);
                }


            }
        }

        return lessonResponse;
    }
}
