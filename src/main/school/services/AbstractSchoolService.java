package main.school.services;
import main.school.data.*;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.InstructorRepository;
import main.school.model.Edition;
import main.school.model.EntityNotFoundException;
import main.school.model.Instructor;

import java.util.Optional;

public interface AbstractSchoolService {


    EditionRepository getEditionRepository();
    InstructorRepository getInstructorRepository();
    CourseRepository getCourseRepository();
    Optional<Instructor> findInstructorById(long id) throws DataException;
    void addOrReplaceInstructorInEdition(long editionId, long instructorID) throws DataException, EntityNotFoundException;
    void commit() throws DataException;
    void rollBack() throws DataException;
    Iterable<Instructor> getAllInstructors() throws DataException;
    public Edition addEdition (Edition edition) throws DataException;
}
