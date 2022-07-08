package main.school.jdbc.implementation;

import main.school.data.DataException;
import main.school.data.abstractions.InstructorRepository;
import main.school.data.abstractions.JdbcRepository;
import main.school.model.Instructor;
import main.school.model.Sector;

import javax.swing.text.html.Option;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcInstructorRepository extends JdbcRepository implements InstructorRepository {

    @Override
    public boolean instructorExists(long idInstructor) {
//        JDBCQueryTemplate<Instructor> template = new JDBCQueryTemplate<>(){
//
//        }
        JDBCQueryTemplate<Instructor> template = new JDBCQueryTemplate<>() {
            @Override
            public Instructor mapItem(ResultSet rset) throws SQLException {
                Instructor inst = new Instructor();
                inst.setId(rset.getLong("ID"));
                return inst;
            }

            @Override
            public void assembleStatement(PreparedStatement stmt, Instructor instructor) {
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
        return null;
    }

    //TODO specializations
    @Override
    public void addInstructor(Instructor inst) throws DataException {
        // serve la chiamata per generare la pk (vedi jdbcSectorRepository)
        JDBCQueryTemplate<Instructor> template = new JDBCQueryTemplate<>() {
            @Override
            public Instructor mapItem(ResultSet rset) throws SQLException {
                return null;
            }

            @Override
            public void assembleStatement(PreparedStatement stmt, Instructor instructor) throws SQLException {
//                stmt.setLong(1, inst.getId());
                stmt.setString(2, instructor.getName());
                stmt.setString(3, instructor.getLastname());
                stmt.setString(4, instructor.getEmail());
                java.sql.Date d = Date.valueOf(instructor.getDob());
                stmt.setDate(5, d);
//                LocalDate localDate = Date.valueOf("2019-01-10").toLocalDate();
            }
        };

        JDBCQueryTemplate<Long> template_multi = new JDBCQueryTemplate<>() {
            @Override
            public Long mapItem(ResultSet rset) throws SQLException {
                return rset.getLong(1);
            }

            @Override
            public void assembleStatement(PreparedStatement stmt, Long i) throws SQLException {}
        };
        template.addOne("INSERT INTO INSTRUCTOR (ID, NAME, LASTNAME, EMAIL, DOB) VALUES( ?, ?, ?, ?, ?)", inst);
        List<Long> ids = new ArrayList<>();


        for (Long id : ids){
            String sql = "INSERT INTO INSTRUCTOR_SECTORS (INSTRUCTOR_ID, SECTOR_ID) VALUES (?,?)";

        }

    }

    @Override
    public Iterable<Instructor> getAll() throws DataException {
        //FASE 1
        String query = "";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
        //FASE 2 APPLICARE IL TEMPLATE PATTERN
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

                /*// list of 1 element (a row contains just 1 specialization)

                List<Sector> specs = new ArrayList<>();
                specs.add(Sector.valueOf(rset.getString("SPECIALIZTION")));
                inst.setSpecialization(specs);
                */
                return inst;
            }

            @Override
            public void assembleStatement(PreparedStatement stmt, Instructor instructor) {}
        };
        JDBCQueryTemplate<Sector> template_sect = new JDBCQueryTemplate<>() {

            @Override
            public Sector mapItem(ResultSet rset) throws SQLException {
                return Sector.valueOf(rset.getString("NAME"));
            }

            @Override
            public void assembleStatement(PreparedStatement stmt, Sector s) {}
        };
        try {
            String sql1 = "SELECT  I.ID, I.NAME, I.LASTNAME, I.EMAIL, I.DOB\n" +
                    "FROM INSTRUCTOR WHERE I.ID = ?";
            Optional<Instructor> oi = template.findById(sql1, instructorId);
            if (oi.isEmpty()){
                return oi;
            }

            Instructor instructor = oi.get();
            //List<String> li = findByIdList(sql2, instructorId);
            String sql2 = "SELECT S.NAME \n" +
                    "FROM SECTOR S \n" +
                    "JOIN INSTRUCTOR_SECTORS inst_sec ON (S.ID = inst_sec.SECTOR_ID) \n" +
                    "JOIN INSTRUCTOR I ON (inst_sec.INSTRUCTOR_ID = I.ID) \n" +
                    "WHERE I.ID = ?";
            List<Sector> sectors = template_sect.findByIdList(sql2, instructorId);
            instructor.setSpecialization(sectors);
            return Optional.of(instructor) ;

        } catch (DataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Instructor> findOlderThanGivenAgeAndMoreThanOneSpecialization(int age) {
        return null;
    }

    @Override
    public boolean updateInstructor(Instructor i) {
        return false;
    }

    @Override
    public void clear() {

    }
}


