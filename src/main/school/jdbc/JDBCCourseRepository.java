package main.school.jdbc;

import main.school.data.DataException;
import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.JDBCRepository;
import main.school.model.Course;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBCCourseRepository extends JDBCRepository implements CourseRepository {
    @Override
    public void addCourse(Course course) throws DataException {
        String query = "INSERT INTO COURSE (ID, TITLE, DURATION, SECTOR_ID, COURSE_LEVEL) VALUES (?, ?, ?, ?, ?)";
        try (
                PreparedStatement statement = conn.prepareStatement(query);
        ) {
            statement.setLong(1, course.getId());
            statement.setString(2, course.getTitle());
            statement.setInt(3, course.getHours());
            //statement.setLong(4, ); aggiungere il sector_id
            statement.setObject(4, course.getLevel());


            statement.execute();
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    @Override
    public Iterable<Course> getCoursesByTitleLike(String title) throws DataException {
        String query = "SELECT *  FROM COURSE C WHERE C.TITLE = ?";
        try(
                PreparedStatement statement = conn.prepareStatement(query);
        ){
            statement.setString(1, title);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                List<Course> courses = new ArrayList<>();
                while(rs.next()){
                    int id = rs.getInt("ID");
                    String name = rs.getString("TITLE");
                    String lastName = rs.getString("DURATION");
                    String email = rs.getString("");
                    //courses.add(new Course(id, title, hours, sector,level));
                    }
                return courses;
                }
            } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Course> getAll(boolean orderByTitle) throws DataException {
        return null;
    }

    @Override
    public Optional<Course> findByID(long idCourse) {
        return Optional.empty();
    }

    @Override
    public void clear() {

    }

    @Override
    public List<Object> variableForSecondQuery(ResultSet rset) {
        return null;
    }

    @Override
    public Object mapItem(ResultSet rset, List enumList) throws SQLException {
        return null;
    }
}
