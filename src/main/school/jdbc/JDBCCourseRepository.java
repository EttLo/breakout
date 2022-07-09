package main.school.jdbc;

import main.school.data.DataException;
import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.JDBCRepository;
import main.school.model.Course;
import main.school.model.Level;
import main.school.model.Sector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBCCourseRepository extends JDBCRepository<Course> implements CourseRepository {
    @Override
    public void addCourse(Course course) throws DataException {
        String query = "INSERT INTO COURSE (ID, TITLE, DURATION, SECTOR_ID, COURSE_LEVEL) " +
                "SELECT C.ID, TITLE, DURATION, SECTOR_ID, COURSE_LEVEL " +
                "FROM COURSE C JOIN SECTOR S ON C.SECTOR_ID = S.ID " +
                "WHERE C.ID = ? AND TITLE = ? AND DURATION = ? AND S.NAME = ? AND COURSE_LEVEL = ?;";

        try (
                PreparedStatement statement = conn.prepareStatement(query);
        ) {
            statement.setLong(1, course.getId());
            statement.setString(2, course.getTitle());
            statement.setInt(3, course.getDuration());
            statement.setString(4, course.getSector().name());
            statement.setObject(4, course.getLevel());


            statement.execute();
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    @Override
    public Iterable<Course> getCoursesByTitleLike(String token) throws DataException {
        String query = "SELECT ID, TITLE, DURATION, SECTOR_ID, COURSE_LEVEL " +
                "FROM COURSE WHERE TITLE LIKE '%?%';"; //check validity
        String querysec = "SELECT NAME FROM SECTOR WHERE ID = ?;";

        try(
                PreparedStatement statement1 = conn.prepareStatement(query);
                PreparedStatement statement2 = conn.prepareStatement(querysec);
        ) {
            statement1.setString(1, token);
            try(
                    ResultSet rs = statement1.executeQuery();
            ) {
                List<Course> courses = new ArrayList<>();
                while(rs.next()){
                    long id = rs.getLong("ID");
                    String title = rs.getString("TITLE");
                    int duration = rs.getInt("DURATION");
                    int sectorId = rs.getInt("SECTOR_ID");
                    String courseLevel = rs.getString("COURSE_LEVEL");
                    statement2.setLong(1, sectorId);
                    String sectorName = "";
                    try(
                            ResultSet rs2 = statement2.executeQuery();
                    ) {
                        rs2.next();
                        sectorName = rs2.getString("NAME");
                    }
                    courses.add(new Course(id, title, duration, Sector.valueOf(sectorName), Level.valueOf(courseLevel)));
                }
                return courses;
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Course> getAll(boolean orderByTitle) throws DataException {
        String query = "";
        if(orderByTitle) {
            query = "SELECT ID, TITLE, DURATION, SECTOR_ID, COURSE_LEVEL FROM COURSE ORDER BY TITLE;";
        } else{
            query = "SELECT ID, TITLE, DURATION, SECTOR_ID, COURSE_LEVEL FROM COURSE;";
        }
        String querysec = "SELECT NAME FROM SECTOR WHERE ID = ?";

        try(
                PreparedStatement ps1 = conn.prepareStatement(query);
                PreparedStatement ps2 = conn.prepareStatement(querysec);
                ResultSet rs = ps1.executeQuery();
                ) {
            List<Course> courses = new ArrayList<>();
            while(rs.next()) {
                long id = rs.getLong("ID");
                String title = rs.getString("TITLE");
                int duration = rs.getInt("DURATION");
                int sectorId = rs.getInt("SECTOR_ID");
                String courseLevel = rs.getString("COURSE_LEVEL");
                String sectorName = "";
                ps2.setInt(1, sectorId);
                try(ResultSet rs2 = ps2.executeQuery();) {
                    while(rs2.next()){
                        sectorName = rs2.getString("NAME");
                    }
                }
                courses.add(new Course(id, title, duration, Sector.valueOf(sectorName), Level.valueOf(courseLevel)));
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Course> findByID(long idCourse) {
        String query = "SELECT ID, TITLE, DURATION, SECTOR_ID, COURSE_LEVEL FROM COURSE " +
                "WHERE ID = ?;";
        String querysec = "SELECT NAME FROM SECTOR WHERE ID = ?";
        try(
                PreparedStatement ps1 = conn.prepareStatement(query);
                PreparedStatement ps2 = conn.prepareStatement(querysec);
                ) {
            ps1.setLong(1, idCourse);
            try(ResultSet rs1 = ps1.executeQuery();) {
                if(rs1.next()) {
                    long id = rs1.getLong("ID");
                    String title = rs1.getString("TITLE");
                    int duration = rs1.getInt("DURATION");
                    int sectorId = rs1.getInt("SECTOR_ID");
                    String courseLevel = rs1.getString("COURSE_LEVEL");
                    String sectorName = "";
                    ps2.setInt(1, sectorId);
                    try(ResultSet rs2 = ps2.executeQuery()) {
                        while(rs2.next()) {
                            sectorName = rs2.getString("NAME");
                        }
                    }
                    return Optional.of(new Course(id, title, duration, Sector.valueOf(sectorName), Level.valueOf(courseLevel)));
                } else{
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean updateCourse(Course course) {
        String query = "UPDATE COURSE SET TITLE = ?, DURATION = ?, SECTOR_ID = ?, COURSE_LEVEL = ? WHERE ID = ?";
        String querysec = "SELECT ID FROM SECTOR WHERE NAME = ?";

        try(
                PreparedStatement updateStatement = conn.prepareStatement(query);
                PreparedStatement selectStatement = conn.prepareStatement(querysec);
                ) {
            updateStatement.setString(1, course.getTitle());
            updateStatement.setInt(2, course.getDuration());
            updateStatement.setString(4, course.getTitle());
            updateStatement.setLong(5, course.getId());
            String sectorName = course.getSector().name();
            int sectorId = 0;
            try(ResultSet rs = selectStatement.executeQuery();) {
                while(rs.next()) {
                   sectorId = rs.getInt("ID");
                }
            }
            updateStatement.setInt(3, sectorId);
            return (updateStatement.executeUpdate() > 0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public List<Object> variableForSecondQuery(ResultSet rs) {
        return null;
    }

    @Override
    public Course mapItem(ResultSet rs, List<String> enumList) throws SQLException {
        return null;
    }


}
