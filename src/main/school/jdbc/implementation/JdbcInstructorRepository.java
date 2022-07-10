package main.school.jdbc.implementation;

import main.school.data.DataException;
import main.school.data.abstractions.InstructorRepository;
import main.school.data.abstractions.JdbcRepository;
import main.school.model.Instructor;
import main.school.model.Sector;
import main.school.model.SectorAssignment;

import javax.swing.text.DateFormatter;
import javax.swing.text.html.Option;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// N.B. le eccezioni sono gestite qui solo perché per alcuni metodi l'interfaccia non le rilancia
// per mancanza di tempo le ho dovute gestire qui
public class JdbcInstructorRepository extends JdbcRepository implements InstructorRepository {

    @Override
    public boolean instructorExists(long idInstructor){
        JDBCQueryTemplate<Instructor> template = new JDBCQueryTemplate<>() {
            @Override
            public Instructor mapItem(ResultSet rset) throws SQLException {
                Instructor inst = new Instructor();
                inst.setId(rset.getLong("ID"));
                return inst;
            }
        };

        try {
//            List<String> parameters = new ArrayList<>();
//            parameters.add(String.valueOf(idInstructor));
            Optional<Instructor> instructor = template.findById(
                    "SELECT ID, NAME FROM INSTRUCTOR WHERE ID = ?", idInstructor);
            return !instructor.isEmpty();
        } catch (DataException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public Iterable<Instructor> getInstructorsBornAfterDateAndMultiSpecialized(LocalDate date) throws DataException {
        JDBCQueryTemplate<Instructor> template = new JDBCQueryTemplate<>() {
            @Override
            public Instructor mapItem(ResultSet rset) throws SQLException {
                Instructor inst = new Instructor();
                inst.setId(rset.getLong("ID"));
                inst.setName(rset.getString("NAME"));
                inst.setLastname(rset.getString("LASTNAME"));
                inst.setEmail(rset.getString("EMAIL"));
                inst.setDob(Date.valueOf(rset.getString("DOB")).toLocalDate());
                return inst;
            }
        };
        JDBCQueryTemplate<SectorAssignment> templateSA = new JDBCQueryTemplate<SectorAssignment>() {
            @Override
            public SectorAssignment mapItem(ResultSet rset) throws SQLException {
                SectorAssignment sa = new SectorAssignment();
                sa.setId(rset.getLong("ID"));
                sa.setSectorId(rset.getLong("SECTOR_ID"));
                sa.setInstructorId(rset.getLong("INSTRUCTOR_ID"));
                return sa;
            }
        };
        String sql1 = "SELECT  ID, NAME, LASTNAME, EMAIL, DOB\n" +
                "FROM INSTRUCTOR WHERE DOB > "+date;
        List<Instructor> instructors = null;
        instructors = template.queryForList(sql1);
        for (Instructor i : instructors){
            i.setSpecialization(new ArrayList<>());
            String sql2 = "SELECT ID, SECTOR_ID, INSTRUCTOR_ID FROM INSTRUCTOR_SECTORS WHERE INSTRUCTOR_ID = ?";
            List<SectorAssignment> sectorAssignments = templateSA.findByIdList(sql2, i.getId());
            if (sectorAssignments.size()>1){
                for (SectorAssignment sa: sectorAssignments) {
                    // we don't need to ask the db to give us sector
                    // ids because we already have them in Sector
                    i.getSpecialization().add(Sector.valueOf(sa.getSectorId()));
                }
            } else {
                instructors.remove(i);
            }
        }
        return instructors;
    }


    @Override
    public void addInstructor(Instructor inst) throws DataException {
        // serve la chiamata per generare la pk (vedi jdbcSectorRepository)
        JDBCUpdateTemplate<Instructor> template = new JDBCUpdateTemplate<>() {

            @Override
            public void assembleStatement(PreparedStatement stmt, Instructor instructor) throws SQLException {
                stmt.setLong(1, instructor.getId());
                stmt.setString(2, instructor.getName());
                stmt.setString(3, instructor.getLastname());
                stmt.setString(4, instructor.getEmail());
                stmt.setDate(5, Date.valueOf(instructor.getDob()));
            }
        };

        String sql1 = "INSERT INTO INSTRUCTOR (ID, NAME, LASTNAME, EMAIL, DOB) VALUES( ?, ?, ?, ?, ?)";
        String genIdSql1 = "SELECT INSTRUCTOR_SEQUENCE.NEXTVAL() FROM DUAL";
        inst = template.addOne(sql1, genIdSql1, inst);


        JDBCUpdateTemplate<SectorAssignment> template_multi = new JDBCUpdateTemplate<>() {
            @Override
            public void assembleStatement(PreparedStatement stmt, SectorAssignment sa) throws SQLException {
                stmt.setLong(1, sa.getId());
                stmt.setLong(2, sa.getSectorId());
                stmt.setLong(3, sa.getInstructorId());
            }
        };

        for (Sector s : inst.getSpecialization()){
            String sql2 = "INSERT INTO INSTRUCTOR_SECTORS (SECTOR_ID, INSTRUCTOR_ID) VALUES (?,?)";
            String genIdSql2 = "SELECT INSTRUCTOR_SEQUENCE.NEXTVAL() FROM DUAL";
            template_multi.addOne(sql2,genIdSql2,new SectorAssignment(s.getId(), inst.getId()));
        }
    }

    @Override
    public Iterable<Instructor> getAll() {
        //FASE 1

        JDBCQueryTemplate<Instructor> templateInstructor = new JDBCQueryTemplate<>() {
            @Override
            public Instructor mapItem(ResultSet rset) throws SQLException {
                Instructor inst = new Instructor();
                inst.setId(rset.getLong("ID"));
                inst.setName(rset.getString("NAME"));
                inst.setLastname(rset.getString("LASTNAME"));
                inst.setEmail(rset.getString("EMAIL"));
                inst.setDob(Date.valueOf(rset.getString("DOB")).toLocalDate());
                return inst;
            }

        };
        JDBCQueryTemplate<SectorAssignment> templateSA = new JDBCQueryTemplate<SectorAssignment>() {
            @Override
            public SectorAssignment mapItem(ResultSet rset) throws SQLException {
                SectorAssignment sa = new SectorAssignment();
                sa.setId(rset.getLong("ID"));
                sa.setSectorId(rset.getLong("SECTOR_ID"));
                sa.setInstructorId(rset.getLong("INSTRUCTOR_ID"));
                return null;
            }
        };


        try {
            String sql1 = "SELECT ID, NAME, LASTNAME, EMAIL, DOB FROM INSTRUCTOR";
            List<Instructor> instructors = null;
            instructors = templateInstructor.queryForList(sql1);
            String sql2 = "SELECT ID SECTOR_ID, INSTRUCTOR_ID FROM INSTRUCTOR_SECTORS";
            List<SectorAssignment> sas = templateSA.queryForList(sql2);
            // assigns to each instructor their specialization
            for (Instructor i : instructors) {
                List<Sector> specs = new ArrayList<>();
                for (SectorAssignment sa : sas){
                    if (sa.getInstructorId() == i.getId()){
                        specs.add(Sector.valueOf(sa.getSectorId()));
                    }
                }
                i.setSpecialization(specs);
            }
            return instructors;
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Instructor> findById(long instructorId) {
        JDBCQueryTemplate<Instructor> template = new JDBCQueryTemplate<>() {

            @Override
            public Instructor mapItem(ResultSet rset) throws SQLException {
                Instructor inst = new Instructor();
                inst.setId(rset.getLong("ID"));
                inst.setName(rset.getString("NAME"));
                inst.setLastname(rset.getString("LASTNAME"));
                inst.setEmail(rset.getString("EMAIL"));
                inst.setDob(Date.valueOf(rset.getString("DOB")).toLocalDate());
                return inst;
            }

        };
        JDBCQueryTemplate<SectorAssignment> templateSA = new JDBCQueryTemplate<SectorAssignment>() {
            @Override
            public SectorAssignment mapItem(ResultSet rset) throws SQLException {
                SectorAssignment sa = new SectorAssignment();
                sa.setId(rset.getLong("ID"));
                sa.setSectorId(rset.getLong("SECTOR_ID"));
                sa.setInstructorId(rset.getLong("INSTRUCTOR_ID"));
                return sa;
            }
        };

        try {
            String sql1 = "SELECT  I.ID, I.NAME, I.LASTNAME, I.EMAIL, I.DOB\n" +
                    "FROM INSTRUCTOR WHERE I.ID = ?";
            Optional<Instructor> oi = template.findById(sql1, instructorId);
            if (oi.isEmpty()){
                return oi;
            }
            Instructor i = oi.get();
            i.setSpecialization(new ArrayList<>());
            String sql2 = "SELECT ID, SECTOR_ID, INSTRUCTOR_ID FROM INSTRUCTOR_SECTORS WHERE INSTRUCTOR_ID = ?";
            List<SectorAssignment> sectorAssignments = templateSA.findByIdList(sql2, i.getId());
            List<Sector> s = i.getSpecialization();
            for (SectorAssignment sa : sectorAssignments) {
                s.add(Sector.valueOf(sa.getSectorId()));
            }
            return oi;

        } catch (DataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Instructor> findOlderThanGivenAgeAndMoreThanOneSpecialization(int age) {
        JDBCQueryTemplate<Instructor> template = new JDBCQueryTemplate<>() {
            @Override
            public Instructor mapItem(ResultSet rset) throws SQLException {
                Instructor inst = new Instructor();
                inst.setId(rset.getLong("ID"));
                inst.setName(rset.getString("NAME"));
                inst.setLastname(rset.getString("LASTNAME"));
                inst.setEmail(rset.getString("EMAIL"));
                inst.setDob(Date.valueOf(rset.getString("DOB")).toLocalDate());
                return inst;
            }
        };
        JDBCQueryTemplate<SectorAssignment> templateSA = new JDBCQueryTemplate<SectorAssignment>() {
            @Override
            public SectorAssignment mapItem(ResultSet rset) throws SQLException {
                SectorAssignment sa = new SectorAssignment();
                sa.setId(rset.getLong("ID"));
                sa.setSectorId(rset.getLong("SECTOR_ID"));
                sa.setInstructorId(rset.getLong("INSTRUCTOR_ID"));
                return sa;
            }
        };

        try {
            String sql1 = "SELECT  ID, NAME, LASTNAME, EMAIL, DOB\n" +
                    "FROM INSTRUCTOR WHERE DOB < "+LocalDate.now().minusYears(age);
            List<Instructor> instructors = null;
            instructors = template.queryForList(sql1);
            for (Instructor i : instructors){
                i.setSpecialization(new ArrayList<>());
                String sql2 = "SELECT ID, SECTOR_ID, INSTRUCTOR_ID FROM INSTRUCTOR_SECTORS WHERE INSTRUCTOR_ID = ?";
                List<SectorAssignment> sectorAssignments = templateSA.findByIdList(sql2, i.getId());
                if (sectorAssignments.size()>1){
                    List<Sector> s = i.getSpecialization();
                    for (SectorAssignment sa: sectorAssignments) {
                        // we don't need to ask the db to give us sector
                        // ids because we already have them in Sector
                        s.add(Sector.valueOf(sa.getSectorId()));
                    }
                } else {
                    instructors.remove(i);
                }
            }
            return instructors;
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean updateInstructor(Instructor i) {
        JDBCUpdateTemplate<Instructor> template = new JDBCUpdateTemplate<>() {

            @Override
            public void assembleStatement(PreparedStatement stmt, Instructor instructor) throws SQLException {
                stmt.setLong(5, instructor.getId());
                stmt.setString(1, instructor.getName());
                stmt.setString(2, instructor.getLastname());
                stmt.setString(3, instructor.getEmail());
                stmt.setDate(4, Date.valueOf(instructor.getDob()));
            }
        };

        JDBCUpdateTemplate<SectorAssignment> template_multi = new JDBCUpdateTemplate<>() {
            @Override
            public void assembleStatement(PreparedStatement stmt, SectorAssignment sa) throws SQLException {
                //stmt.setLong(1, sa.getId());
                stmt.setLong(1, sa.getSectorId());
                stmt.setLong(2, sa.getInstructorId());
            }
        };

        String sql1 = "UPDATE INSTRUCTOR\n" +
                "SET NAME = ?,\n" +
                "    LASTNAME = ?,\n" +
                "    EMAIL = ?,\n" +
                "    DOB = ?,\n" +
                "WHERE ID = ?;";
        String sql2 = "UPDATE INSTRUCTOR_SECTORS\n" +
                "SET SECTOR_ID = ?,\n" +
                "WHERE INSTRUCTOR_ID = ?;";
        try {
            if (!template.update(sql1, i)){
                return false;
            }
            for (Sector s: i.getSpecialization()){
                template_multi.update(sql2, new SectorAssignment(s.getId(), i.getId()));
            }
            return true;
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void clear() {

    }
}


