package org.timeTable.models;

import com.google.gson.*;
import org.apache.commons.lang3.Streams;
import org.timeTable.LiteSQL;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CourseResponseDeserializer implements JsonDeserializer<CourseResponse> {

    @Override
    public CourseResponse deserialize(JsonElement json, Type type,
                                      JsonDeserializationContext context) throws JsonParseException {

        JsonArray jArray = (JsonArray) json;

        CourseResponse courseResponse = new CourseResponse();

        for (int i = 0; i < jArray.size(); i++) {
            JsonObject jObject = (JsonObject) jArray.get(i);
            
            int id = -1;
            try {
                ResultSet set = LiteSQL.onQuery("SELECT id FROM course WHERE name = '" + jObject.get("name").getAsString() + "'");
                id = set.getInt("id");
                set.close();
            } catch (SQLException ignored) {
                //ignore the courses that are not in the database
            }



            Course course = new Course(id, jObject.get("id").getAsInt(), jObject.get("name").getAsString(),jObject.get("longName").getAsString());
            courseResponse.elements.add(course);
        }

        return courseResponse;
    }
}
