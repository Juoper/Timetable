package org.timeTable.models;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LessonResponse {
    public ArrayList<Lesson> elements;

    public LessonResponse() {
        elements = new ArrayList<>();
    }
}


