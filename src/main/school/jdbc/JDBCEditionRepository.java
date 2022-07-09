package main.school.jdbc;

import main.school.data.DataException;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.JDBCRepository;
import main.school.model.Edition;
import main.school.model.Instructor;
import main.school.model.Level;
import main.school.model.Sector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JDBCEditionRepository  extends JDBCRepository implements EditionRepository {
    @Override
    public Iterable<Edition> getAll() throws DataException {
        return null;
    }

    @Override
    public Iterable<Instructor> getInstructorFromSectorAndLevel(Sector sector, Level level) throws DataException {
        return null;
    }

    @Override
    public void addEdition(Edition CourseEdition) throws DataException {

    }

    @Override
    public Iterable<Edition> getEditionsFromCourseId(long idCourse) throws DataException {
        return null;
    }

    @Override
    public Optional<Edition> findEditionById(long courseEditionId) {
        return Optional.empty();
    }

    @Override
    public void clear() {

    }

    @Override
    public List<Object> variableForSecondQuery(ResultSet rs) {
        return null;
    }

    @Override
    public Object mapItem(ResultSet rset, List enumList) throws SQLException {
        return null;
    }
}
