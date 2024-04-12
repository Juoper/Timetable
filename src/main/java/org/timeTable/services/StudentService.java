package org.timeTable.services;

import org.timeTable.persistence.student.Student;

public interface StudentService {
    Student getOrCreateStudent(String fullname);
}
