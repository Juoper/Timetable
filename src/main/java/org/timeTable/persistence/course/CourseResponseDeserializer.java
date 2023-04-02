package org.timeTable.persistence.course;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.CommunicationLayer.CommunicationLayer;
import org.timeTable.LiteSQL;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CourseResponseDeserializer implements JsonDeserializer<CourseResponse> {

    @Autowired
    CourseRepository courseRepository;
    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);

    @Override
    public CourseResponse deserialize(JsonElement json, Type type,
                                      JsonDeserializationContext context) throws JsonParseException {

        JsonArray jArray = (JsonArray) json;

        CourseResponse courseResponse = new CourseResponse();

        for (int i = 0; i < jArray.size(); i++) {
            JsonObject jObject = (JsonObject) jArray.get(i);

            List<Course> matchingCourses = courseRepository.findByName(jObject.get("name").getAsString());

            if (matchingCourses.isEmpty()){
                logger.warn("Found course without matching database entry: " + jObject.get("name").getAsString());
            }

            Course course = matchingCourses.get(0);

            courseResponse.elements.add(course);
        }

        return courseResponse;
    }
}
