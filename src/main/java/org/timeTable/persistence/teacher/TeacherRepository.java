package org.timeTable.persistence.teacher;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeacherRepository extends CrudRepository<Teacher, Long> {
    List<Teacher> findByAbbreviation(String abbreviation);
}
