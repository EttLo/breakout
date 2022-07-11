package main.school.data.jdbc;

import main.school.data.DataException;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.JDBCRepository;
import main.school.model.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBCEditionRepository  extends JDBCRepository<Edition> implements EditionRepository {
    @Override
    public Iterable<Edition> getAll() throws DataException {
        String query = "SELECT E.ID AS EDITION_ID, START_DATE, END_DATE, COST, " +
                "C.ID AS COURSE_ID, TITLE, DURATION, S.NAME AS SECTOR_NAME, COURSE_LEVEL, " +
                "I.ID AS INSTRUCTOR_ID, I.NAME AS INSTRUCTOR_NAME, LAST_NAME, DOB, EMAIL " +
                "FROM EDITION E JOIN COURSE C ON E.COURSE_ID = C.ID " +
                "JOIN INSTRUCTOR I ON E.INSTRUCTOR_ID = I.ID " +
                "JOIN SECTOR S ON C.SECTOR_ID = S.ID";
        String querysec = "SELECT S.NAME AS SECTOR_NAME FROM INSTRUCTOR_SECTOR ISE " +
                "JOIN SECTOR S ON (ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?";
        return queriesForList(query, querysec, new ArrayList<>());
    }

    @Override
    public Iterable<Instructor> getInstructorFromSectorAndLevel(Sector sector, Level level) throws DataException {
        String query = "SELECT I.ID AS INSTRUCTOR_ID, I.NAME AS INSTRUCTOR_NAME, LAST_NAME, EMAIL, DOB " +
                "FROM INSTRUCTOR I JOIN EDITION E ON I.ID = E.INSTRUCTOR_ID " +
                "JOIN COURSE C ON C.ID = E.COURSE_ID " +
                "JOIN SECTOR S ON S.ID = C.SECTOR_ID " +
                "WHERE C.COURSE_LEVEL = ? AND S.NAME = ?";
        String querysec = "SELECT S.NAME AS SECTOR_NAME FROM INSTRUCTOR_SECTOR ISE " +
                "JOIN SECTOR S ON (ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?";
        String sectorName = sector.name();
        String courseLevel = level.name();

        try(
                PreparedStatement ps1 = conn.prepareStatement(query);
                PreparedStatement ps2 = conn.prepareStatement(querysec);

                ) {
            ps1.setString(1, sectorName);
            ps1.setString(2, courseLevel);
            try(ResultSet rs = ps1.executeQuery()){
                List<Instructor> instructors = new ArrayList<>();
                while(rs.next()) {
                    long instructorId = rs.getLong("INSTRUCTOR_ID");
                    String name = rs.getString("INSTRUCTOR_NAME");
                    String lastName = rs.getString("LAST_NAME");
                    String email = rs.getString("EMAIL");
                    LocalDate dob = rs.getDate("DOB").toLocalDate();
                    ps2.setLong(1, instructorId);
                    List<Sector> sectors = new ArrayList<>();
                    try( ResultSet rsSec = ps2.executeQuery();
                    ){
                        sectors.clear();
                        while(rsSec.next()){
                            sectors.add(Sector.valueOf(rsSec.getString("NAME")));
                        }
                    }
                    instructors.add(new Instructor(instructorId, name, lastName, dob, email, sectors));
                }
                return instructors;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addEdition(Edition edition) throws DataException {
        String query = "INSERT INTO EDITION (ID, COURSE_ID, START_DATE, END_DATE, COST, INSTRUCTOR_ID) " +
                "SELECT MAX(ID)+1, COURSE_ID, START_DATE, END_DATE, COST, INSTRUCTOR_ID " +
                "FROM EDITION WHERE COURSE_ID = ? AND START_DATE = ? AND END_DATE = ? " +
                "AND COST = ? AND INSTRUCTOR_ID = ?";
        long courseId = edition.getCourse().getId();
        long instructorId = edition.getInstructor().getId();

        try (
                PreparedStatement statement = conn.prepareStatement(query);
        ) {
            statement.setLong(1, courseId);
            statement.setDate(2, Date.valueOf(edition.getStartDate()));
            statement.setDate(3, Date.valueOf(edition.getEndDate()));
            statement.setDouble(4, edition.getCost());
            statement.setLong(5, instructorId);
            statement.execute();
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }

    }

    @Override
    public Iterable<Edition> getEditionsFromCourseId(long courseId) throws DataException {
        String query = "SELECT E.ID AS EDITION_ID, START_DATE, END_DATE, COST, " +
                "C.ID AS COURSE_ID, TITLE, DURATION, S.NAME AS SECTOR_NAME, COURSE_LEVEL, " +
                "I.ID AS INSTRUCTOR_ID, I.NAME AS INSTRUCTOR_NAME, LAST_NAME, DOB, EMAIL " +
                "FROM EDITION E JOIN COURSE C ON E.COURSE_ID = C.ID " +
                "JOIN INSTRUCTOR I ON E.INSTRUCTOR_ID = I.ID " +
                "JOIN SECTOR S ON C.SECTOR_ID = S.ID " +
                "WHERE COURSE_ID = ?";
        String querysec = "SELECT S.NAME AS SECTOR_NAME FROM INSTRUCTOR_SECTOR ISE " +
                "JOIN SECTOR S ON (ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?";
        List<Object> typesToSet = new ArrayList<>();
        typesToSet.add(courseId);
        return queriesForList(query, querysec, typesToSet);
    }

    @Override
    public Optional<Edition> findEditionById(long editionId) throws DataException {
        String query = "SELECT E.ID AS EDITION_ID, START_DATE, END_DATE, COST, " +
                "C.ID AS COURSE_ID, TITLE, DURATION, S.NAME AS SECTOR_NAME, COURSE_LEVEL, " +
                "I.ID AS INSTRUCTOR_ID, I.NAME AS INSTRUCTOR_NAME, LAST_NAME, DOB, EMAIL " +
                "FROM EDITION E JOIN COURSE C ON E.COURSE_ID = C.ID " +
                "JOIN INSTRUCTOR I ON E.INSTRUCTOR_ID = I.ID " +
                "JOIN SECTOR S ON C.SECTOR_ID = S.ID " +
                "WHERE E.ID = ?";
        String querysec = "SELECT S.NAME AS SECTOR_NAME FROM INSTRUCTOR_SECTOR ISE " +
                "JOIN SECTOR S ON (ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?";
        List<Object> typesToSet = new ArrayList<>();
        typesToSet.add(editionId);
        List<Edition> editions = queriesForList(query, querysec, typesToSet);
        Optional<Edition> edition = editions.size() == 0 ? Optional.empty() : Optional.of(editions.get(0));
        return edition;
    }

    @Override
    public void clear() {

    }

    @Override
    public void assembleCreateStatement(Edition entity, PreparedStatement ps) {

    }

    @Override
    public List<Object> variableForSecondQuery(ResultSet rs) throws DataException {
        List<Object> variable = new ArrayList<>();
        try {
            variable.add(rs.getInt("ID"));
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
        return variable;
    }

    @Override
    public Edition mapItem(ResultSet rs, List<String> enumList) throws SQLException {
        //create edition_course
        Course course = new Course();
        course.setId(rs.getLong("COURSE_ID"));
        course.setTitle(rs.getString("TITLE"));
        course.setDuration(rs.getInt("DURATION"));
        course.setSector(Sector.valueOf(rs.getString("SECTOR_NAME")));
        course.setLevel(Level.valueOf(rs.getString("COURSE_LEVEL")));
        //create edition_instructor
        Instructor instructor = new Instructor();
        instructor.setId(rs.getLong("INSTRUCTOR_ID"));
        instructor.setName(rs.getString("NAME"));
        instructor.setLastname(rs.getString("LAST_NAME"));
        instructor.setDob(rs.getDate("DOB").toLocalDate());
        instructor.setEmail(rs.getString("EMAIL"));
        List<Sector> sectors = new ArrayList<>();
        for (String s: enumList) {
            sectors.add(Sector.valueOf(s));
        }
        instructor.setSpecialization(sectors);
        //set the rest of edition's variables
        long id = rs.getLong("EDITION_ID");
        LocalDate startDate = rs.getDate("START_DATE").toLocalDate();
        LocalDate endDate = rs.getDate("END_DATE").toLocalDate();
        double cost = rs.getDouble("COST");
        return new Edition(id, course, startDate, endDate, cost, instructor);
    }

    @Override
    public Edition mapObject(ResultSet rs) throws SQLException {
        return null;
    }

}
