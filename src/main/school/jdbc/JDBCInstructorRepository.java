package main.school.jdbc;

import main.school.data.DataException;
import main.school.data.abstractions.InstructorRepository;
import main.school.data.abstractions.JDBCRepository;
import main.school.model.Instructor;
import main.school.model.Sector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBCInstructorRepository extends JDBCRepository implements InstructorRepository {


    @Override
    public boolean instructorExists(long idInstructor) {
        String query = "SELECT *  FROM INSTRUCTOR WHERE ID = ?";
        try(
            PreparedStatement statement = conn.prepareStatement(query);
            ){
            statement.setLong(1, idInstructor);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                return rs.next();
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Instructor> getInstructorsBornAfterDateAndMultiSpecialized(LocalDate date) throws DataException {
        String query = "SELECT I.* " +
                "FROM INSTRUCTOR I JOIN INSTRUCTOR_SECTOR ISE ON (I.ID = ISE.INSTRUCTOR_ID) " +
                "WHERE I.DOB > ? " +
                "GROUP BY I.ID, I.NAME, I.LASTNAME, I.EMAIL, I.DOB" +
                "HAVING COUNT (ISE.SECTOR_ID) > 1;";
        String querysec ="SELECT S.NAME " + "FROM INSTRUCTOR_SECTOR ISE JOIN SECTOR S ON(ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?;";
        try(
                PreparedStatement statement = conn.prepareStatement(query);
                PreparedStatement statementsec = conn.prepareStatement(querysec);
        ){
            Date sqldate = Date.valueOf(date);
            statement.setDate(1, sqldate);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                List<Instructor> instructors = new ArrayList<>();
                while(rs.next()){
                    int id = rs.getInt("ID");
                    String name = rs.getString("NAME");
                    String lastName = rs.getString("LASTNAME");
                    LocalDate dob = rs.getDate("DOB").toLocalDate();
                    String email = rs.getString("EMAIL");
                    statementsec.setInt(1, id);
                    List<Sector> sectors = new ArrayList<>();
                    try( ResultSet rsSec = statementsec.executeQuery();
                    ){
                        sectors.clear();
                        while(rsSec.next()){
                            sectors.add(Sector.valueOf(rsSec.getString("NAME")));
                        }
                    }
                    instructors.add(new Instructor(id, name, lastName,dob,email,sectors));
                }
                return instructors;
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addInstructor(Instructor instructor) throws DataException {
        String query = "INSERT INTO INSTRUCTOR (ID, NAME, LASTNAME, EMAIL, DOB) VALUES (?, ?, ?, ?, ?)";
        try (
                PreparedStatement statement = conn.prepareStatement(query);
        ) {
            statement.setLong(1, instructor.getId());
            statement.setString(2, instructor.getName());
            statement.setString(3, instructor.getLastname());
            statement.setString(4, instructor.getEmail());
            statement.setDate(5, Date.valueOf(instructor.getDob()));

            statement.execute();
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    @Override
    public Iterable<Instructor> getAll() throws DataException {
        String query = "SELECT *  FROM INSTRUCTOR";
        try(
               Statement statement = conn.createStatement();
               ResultSet rs = statement.executeQuery(query);
        ){
          List<Instructor> instructors = new ArrayList<>();
          while(rs.next()){
                int 
          }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Instructor> findById(long instructorId) {
        return Optional.empty();
    }

    @Override
    public Iterable<Instructor> findOlderThanGivenAgeAndMoreThanOneSpecialization(int age) {
        String query = "SELECT I.* " +
                "FROM INSTRUCTOR I JOIN INSTRUCTOR_SECTOR ISE ON (I.ID = ISE.INSTRUCTOR_ID) " +
                "WHERE CURRENT_DATE < add_months(I.DOB, 12 * ?) " +
                "GROUP BY I.ID, I.NAME, I.LASTNAME, I.EMAIL, I.DOB" +
                "HAVING COUNT (ISE.SECTOR_ID) > 1;";
        String querysec ="SELECT S.NAME " + "FROM INSTRUCTOR_SECTOR ISE JOIN SECTOR S ON(ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?;";
        try(
                PreparedStatement statement = conn.prepareStatement(query);
                PreparedStatement statementsec = conn.prepareStatement(querysec);
        ){
            statement.setInt(1, age);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                List<Instructor> instructors = new ArrayList<>();
                while(rs.next()){
                    int id = rs.getInt("ID");
                    String name = rs.getString("NAME");
                    String lastName = rs.getString("LASTNAME");
                    LocalDate dob = rs.getDate("DOB").toLocalDate();
                    String email = rs.getString("EMAIL");
                    statementsec.setInt(1, id);
                    List<Sector> sectors = new ArrayList<>();
                    try( ResultSet rsSec = statementsec.executeQuery();
                    ){
                        sectors.clear();
                        while(rsSec.next()){
                          sectors.add(Sector.valueOf(rsSec.getString("NAME")));
                        }
                    }
                    instructors.add(new Instructor(id, name, lastName,dob,email,sectors));
                }
                return instructors;
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {

    }
}
