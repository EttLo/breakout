package main.school.data.jdbc;

import main.school.data.DataException;
import main.school.data.abstractions.JDBCRepository;
import main.school.data.abstractions.SectorRepository;
import main.school.data.abstractions.WithId;
import main.school.model.Instructor;
import main.school.model.Sector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCSectorRepository extends JDBCRepository<Sector> implements SectorRepository {

    private static String sql = "SELECT S.ID, S.NAME FROM SECTOR S JOIN INSTRUCTOR_SECTOR ISE " +
            "ON S.ID = ISE.SECTOR_ID AND ISE.INSTRUCTOR_ID = ?";
    @Override
    public void assembleCreateStatement(Sector entity, PreparedStatement ps) {

    }

    @Override
    public List<Object> variableForSecondQuery(ResultSet rs) throws DataException {
        return null;
    }

    @Override
    public Sector mapItem(ResultSet rs, List<String> enumList) throws SQLException {
        return null;
    }

    @Override
    public Sector mapObject(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public Iterable<Sector> getSectorsForInstructor(long instructorId) throws DataException {
        return queryForObjects(sql, instructorId);
    }
}
