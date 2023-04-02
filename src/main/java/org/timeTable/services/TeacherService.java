package org.timeTable.services;

import org.springframework.stereotype.Service;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.teacher.Teacher;

@Service
public interface TeacherService {
     void addCourseToTeacher(Teacher teacher, Course course);
    Teacher getTeacherByAbbreviation(String teacherAbbreviation);
}
