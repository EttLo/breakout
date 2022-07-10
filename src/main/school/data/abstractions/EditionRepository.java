package main.school.data.abstractions;

import main.school.data.DataException;
import main.school.model.Edition;
import main.school.model.Instructor;
import main.school.model.Level;
import main.school.model.Sector;

import java.util.Optional;

public interface EditionRepository {

    Iterable<Edition> getAll() throws DataException;
    public Iterable<Instructor> getInstructorFromSectorAndLevel(Sector sector, Level level) throws DataException;
    void addEdition(Edition edition)throws DataException;
    Iterable<Edition> getEditionsFromCourseId(long courseId) throws DataException;
    Optional<Edition> findEditionById(long editionId) throws DataException;
    void clear ();
}
