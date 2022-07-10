package main.school.data.abstractions;

import main.school.data.DataException;
import main.school.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    void addCourse(Course course)throws DataException;
    Iterable<Course> getCoursesByTitleLike(String title) throws DataException;
    Iterable<Course> getAll(boolean orderByTitle) throws DataException;
    Optional<Course> findByID(long courseId) throws DataException;
    void clear();
}
