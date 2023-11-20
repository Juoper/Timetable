package org.timeTable.persistence.student;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
    Student findByPrenameAndSurname(String prename, String surname);

    List<Student> findAllByPrenameOrAndSurname(String prename, String surname);
}
