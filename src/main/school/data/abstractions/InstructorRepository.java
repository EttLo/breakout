package main.school.data.abstractions;

import main.school.data.DataException;
import main.school.model.Instructor;

import java.time.LocalDate;
import java.util.Optional;

public interface InstructorRepository {

    boolean instructorExists(long idInstructor);
    Iterable<Instructor> getInstructorsBornAfterDateAndMultiSpecialized(LocalDate date)throws DataException;
    void addInstructor(Instructor instructor) throws DataException;
    Iterable<Instructor> getAll() throws DataException;
    Optional<Instructor> findById(long instructorId);
    Iterable<Instructor> findOlderThanGivenAgeAndMoreThanOneSpecialization(int age);
    void clear ();

}
