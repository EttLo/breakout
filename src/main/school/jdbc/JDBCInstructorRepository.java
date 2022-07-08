package main.school.jdbc;

import main.school.data.DataException;
import main.school.data.abstractions.InstructorRepository;
import main.school.model.Instructor;

import java.time.LocalDate;
import java.util.Optional;

public class JDBCInstructorRepository implements InstructorRepository {


    @Override
    public boolean instructorExists(long idInstructor) {
        return false;
    }

    @Override
    public Iterable<Instructor> getInstructorsBornAfterDateAndMultiSpecialized(LocalDate date) throws DataException {
        return null;
    }

    @Override
    public void addInstructor(Instructor instructor) throws DataException {

    }

    @Override
    public Iterable<Instructor> getAll() throws DataException {
        return null;
    }

    @Override
    public Optional<Instructor> findById(long instructorId) {
        return Optional.empty();
    }

    @Override
    public Iterable<Instructor> findOlderThanGivenAgeAndMoreThanOneSpecialization(int age) {
        return null;
    }

    @Override
    public void clear() {

    }
}
