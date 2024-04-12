package org.timeTable.persistence.course;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.CommunicationLayer.CommunicationLayer;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class CourseResponseDeserializer implements JsonDeserializer<CourseResponse> {

    private final Logger logger = LoggerFactory.getLogger(CommunicationLayer.class);
    private final CourseRepository courseRepository;

    @Autowired
    public CourseResponseDeserializer(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseResponse deserialize(JsonElement json, Type type,
                                      JsonDeserializationContext context) throws JsonParseException {

        JsonArray jArray = (JsonArray) json;

        CourseResponse courseResponse = new CourseResponse();

        for (int i = 0; i < jArray.size(); i++) {
            JsonObject jObject = (JsonObject) jArray.get(i);

            List<Course> matchingCourses = courseRepository.findByName(jObject.get("name").getAsString());

            if (matchingCourses.size() == 0){
                logger.warn("Found course without matching database entry: " + jObject.get("name").getAsString());
            } else {
                Course course = matchingCourses.get(0);
                course.lessons = new HashSet<>();
                course.setUntisId(jObject.get("id").getAsInt());
                courseRepository.save(course);

                if (courseResponse.elements.stream().noneMatch(c -> Objects.equals(c.getName(), course.getName()))) {
                    courseResponse.elements.add(course);
                }
            }
        }

        return courseResponse;
    }
}
