package org.timeTable.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.teacher.Teacher;
import org.timeTable.persistence.teacher.TeacherRepository;

import java.util.List;

public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public void addCourseToTeacher(Teacher teacher, Course course) {
        //TODO check if teacher already has course
        teacher.addCourse(course);
        teacherRepository.save(teacher);
    }

    @Override
    public Teacher getTeacherByAbbreviation(String teacherAbbreviation) {
        List<Teacher> teachersByAbbreviation = teacherRepository.findByAbbreviation(teacherAbbreviation);
        Teacher teacher = null;
        if (teachersByAbbreviation == null || teachersByAbbreviation.size() == 0) {
            teacher = new Teacher(teacherAbbreviation);
            teacherRepository.save(teacher);
        } else {
            teacher = teachersByAbbreviation.get(0);
        }
        return teacher;
    }
}
