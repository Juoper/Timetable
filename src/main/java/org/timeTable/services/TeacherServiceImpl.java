package org.timeTable.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.persistence.course.Course;
import org.timeTable.persistence.teacher.Teacher;
import org.timeTable.persistence.teacher.TeacherRepository;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public void addCourseToTeacher(Teacher teacher, Course course) {
        if (teacher.getCourses() == null){
            teacher.addCourse(course);
        } else if (!teacher.getCourses().contains(course)){
            teacher.addCourse(course);
        }

        teacherRepository.save(teacher);
    }

    @Override
    public Teacher getTeacherByAbbreviation(String teacherAbbreviation) {
        List<Teacher> teachersByAbbreviation = teacherRepository.findByAbbreviation(teacherAbbreviation);
        Teacher teacher = null;
        if (teachersByAbbreviation == null || teachersByAbbreviation.size() == 0) {
            teacher = new Teacher(teacherAbbreviation);
            teacherRepository.save(teacher);
        } else if (teachersByAbbreviation.size() == 1) {
            teacher = teachersByAbbreviation.get(0);
        } else {
            throw new RuntimeException("More than one teacher with the same abbreviation: " + teacherAbbreviation);

        }
        return teacher;
    }
}
