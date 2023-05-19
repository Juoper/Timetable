package org.timeTable.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timeTable.persistence.student.Student;
import org.timeTable.persistence.student.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student getOrCreateStudent(String fullname) {
        fullname = fullname.replace("\r\n", "");
        fullname = fullname.trim();
        String prename = fullname.split(" ")[0];
        String surname = fullname.split(" ", 2)[1];

        Student student = studentRepository.findByPrenameAndSurname(prename, surname);
        if (student == null) {
            student = studentRepository.save(new Student(prename, surname));
        }

        return student;
    }
}
