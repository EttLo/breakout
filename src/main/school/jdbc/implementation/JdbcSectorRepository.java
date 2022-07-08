package main.school.jdbc.implementation;

import main.school.data.DataException;
import main.school.data.abstractions.SectorRepository;
import main.school.model.Sector;
import main.school.model.SectorAssignment;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcSectorRepository implements SectorRepository {
    @Override
    public void insertSectorForInstructor(SectorAssignment sa) throws DataException {
        JDBCUpdateTemplate<SectorAssignment> template = new JDBCUpdateTemplate<SectorAssignment>() {
            @Override
            public void assembleStatement(PreparedStatement stmt, SectorAssignment sectorAssignment) throws SQLException {
                stmt.setLong(1, sectorAssignment.getId());
                stmt.setLong(2, sectorAssignment.getSectorId());
                stmt.setLong(3, sectorAssignment.getInstructorId());
            }
        };
        String sql = "INSERT INTO INSTRUCTOR_SECTORS (ID, SECTOR_ID, INSTRUCTOR_ID) VALUES (?, ?, ?)";
        String genIdSql = "SELECT INSTRUCTOR_SEQUENCE.NEXTVAL() FROM DUAL";
        template.addOne(sql, genIdSql, sa);
    }
}
