package org.timeTable.models;

import com.google.gson.*;
import org.apache.commons.lang3.Streams;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CourseResponseDeserializer implements JsonDeserializer<CourseResponse> {

    @Override
    public CourseResponse deserialize(JsonElement json, Type type,
                                      JsonDeserializationContext context) throws JsonParseException {

        JsonArray jArray = (JsonArray) json;

        CourseResponse courseResponse = new CourseResponse();

        for (int i = 1; i < jArray.size(); i++) {
            JsonObject jObject = (JsonObject) jArray.get(i);
            //assuming you have the suitable constructor...

            Course course = new Course(jObject.get("id").getAsInt(),jObject.get("name").getAsString(),jObject.get("longName").getAsString());
            courseResponse.elements.add(course);
        }

        return courseResponse;
    }
}
