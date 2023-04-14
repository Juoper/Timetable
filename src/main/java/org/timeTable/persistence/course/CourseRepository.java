package org.timeTable.persistence.course;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findByName(String name);

    List<Course> findByShortSubject(String courseAbbreviation);
}
