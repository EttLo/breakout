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

public class JDBCInstructorRepository extends JDBCRepository<Instructor> implements InstructorRepository {

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
        List<Object> typesToSet = new ArrayList<>();
        typesToSet.add(Date.valueOf(date));
        List<Instructor> instructors = queriesForList(query, querysec, typesToSet);
        return instructors;
        /*
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
            e.printStackTrace();
            return null;
        }

         */
    }

    @Override
    public void addInstructor(Instructor instructor) throws DataException {
        String query1 = "INSERT INTO INSTRUCTOR (ID, NAME, LASTNAME, EMAIL, DOB)" +
                "SELECT MAX(ID)+1, NAME, LASTNAME, EMAIL, DOB" +
                "FROM INSTRUCTOR" +
                "WHERE NAME = ? AND LASTNAME = ? AND EMAIL = ? AND DOB = ?";
        String query2 = "INSERT INTO INSTRUCTOR_SECTOR (ID, INSTRUCTOR_ID, SECTOR_ID)" +
                " SELECT MAX(ISE.ID)+1, INSTRUCTOR_ID, SECTOR_ID" +
                " FROM INSTRUCTOR_SECTOR ISE JOIN SECTOR S ON SECTOR_ID = S.ID" +
                " WHERE S.NAME = ? AND INSTRUCTOR_ID = ?";
        /*
        List<Object> typesToSet = new ArrayList<>();
        typesToSet.add(instructor.getName());
        typesToSet.add(instructor.getLastname());
        typesToSet.add(instructor.getEmail());
        typesToSet.add(Date.valueOf(instructor.getDob()));
        queriesForList(query1, query2, typesToSet);
        // problem with second query input
        //variableForSecondQuery only takes the instructor id and doesn't provide s.name
         */

        try (
                PreparedStatement statement = conn.prepareStatement(query1);
                PreparedStatement ps = conn.prepareStatement(query2);
        ) {
            statement.setString(1, instructor.getName());
            statement.setString(2, instructor.getLastname());
            statement.setString(3, instructor.getEmail());
            statement.setDate(4, Date.valueOf(instructor.getDob()));
            statement.execute();
            for (Sector s: instructor.getSpecialization()) {
                ps.setString(1, s.name());
                ps.setLong(2, instructor.getId());

            }
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }


    }

    @Override
    public Iterable<Instructor> getAll() throws DataException {
        String query = "SELECT * FROM INSTRUCTOR;";
        String querysec ="SELECT S.NAME FROM INSTRUCTOR_SECTOR ISE JOIN SECTOR S ON(ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?;";
        List<Instructor> instructors = queriesForList(query, querysec, new ArrayList<>());
        return instructors;

        /*
        try(
               Statement statement = conn.createStatement();
               ResultSet rs = statement.executeQuery(query);
               PreparedStatement statementsec = conn.prepareStatement(querysec);
        ){
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
        }catch(SQLException e){
            throw new RuntimeException(e);
        }

         */
    }

    @Override
    public Optional<Instructor> findById(long instructorId) throws DataException {
        String query = "SELECT ID, NAME, LASTNAME, EMAIL, DOB FROM INSTRUCTOR " +
                "WHERE ID = ?;";
        String querysec ="SELECT S.NAME FROM INSTRUCTOR_SECTOR ISE JOIN SECTOR S ON (ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?;";
        List<Object> typesToSet = new ArrayList<>();
        List<Instructor> instructors = queriesForList(query, querysec, typesToSet);
        Optional<Instructor> instructor = instructors.size() == 0 ? Optional.empty() : Optional.of(instructors.get(0));
        return instructor;
        /*
        try (
                PreparedStatement statement = conn.prepareStatement(query);
                PreparedStatement statementsec = conn.prepareStatement(querysec);
        ) {
            statement.setLong(1, instructorId);

            try (
                    ResultSet rs = statement.executeQuery();

            ) {
                if (rs.next()) {
                    long id = rs.getLong("ID");
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
                   return Optional.of(new Instructor(id, name, lastName,dob,email,sectors));
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

         */
    }

    @Override
    public Iterable<Instructor> findOlderThanGivenAgeAndMoreThanOneSpecialization(int age) throws DataException {
        String query = "SELECT I.* " +
                "FROM INSTRUCTOR I JOIN INSTRUCTOR_SECTOR ISE ON (I.ID = ISE.INSTRUCTOR_ID) " +
                "WHERE CURRENT_DATE < add_months(I.DOB, 12 * ?) " +
                "GROUP BY I.ID, I.NAME, I.LASTNAME, I.EMAIL, I.DOB" +
                "HAVING COUNT (ISE.SECTOR_ID) > 1;";
        String querysec ="SELECT S.NAME " + "FROM INSTRUCTOR_SECTOR ISE JOIN SECTOR S ON(ISE.SECTOR_ID = S.ID) " +
                "WHERE ISE.INSTRUCTOR_ID = ?;";
        List<Object> typesToSet = new ArrayList<>();
        typesToSet.add(age);
        List<Instructor> instructors = queriesForList(query, querysec, typesToSet);
        return instructors;
        /*
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
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

         */
    }

    @Override
    public boolean updateInstructor(Instructor instructor) {
        String updateQuery = "UPDATE INSTRUCTOR SET NAME = ?, LASTNAME = ?, DOB = ?, EMAIL = ? WHERE ID = ?;";
        String insertQuery = "INSERT INTO INSTRUCTOR_SECTOR (ID, INSTRUCTOR_ID, SECTOR_ID)" +
                "SELECT MAX(ISE.ID)+1, INSTRUCTOR_ID, SECTOR_ID " +
                "FROM INSTRUCTOR_SECTOR ISE JOIN SECTOR S ON SECTOR_ID = S.ID " +
                "WHERE S.NAME = ? AND INSTRUCTOR_ID = ?;";
        String deleteQuery = "DELETE FROM INSTRUCTOR_SECTOR WHERE INSTRUCTOR_ID = ? AND SECTOR_ID IN " +
                "(SELECT ID FROM SECTOR WHERE NAME NOT IN ?);";
        String selectQuery = "SELECT DISTINCT S.NAME FROM INSTRUCTOR_SECTOR ISE " +
                "JOIN SECTOR S ON (S.ID = ISE.SECTOR_ID);";
        try (
                PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery);
                PreparedStatement selectStatement = conn.prepareStatement(selectQuery);
        ) {
            //update INSTRUCTOR table
            updateStatement.setString(1, instructor.getName());
            updateStatement.setString(2, instructor.getLastname());
            updateStatement.setDate(3, Date.valueOf(instructor.getDob()));
            updateStatement.setString(4, instructor.getEmail());
            updateStatement.setLong(5, instructor.getId());
            int updateBool = updateStatement.executeUpdate();

            //delete rows from INSTRUCTOR_SECTOR table
            List<Sector> sectors = instructor.getSpecialization();
            String sectorString = "('GIANCARLO','"; //fake value to check if list is empty
            for (Sector s: sectors) {
                sectorString += s.name() + "','";
            }
            sectorString = sectorString.substring(0, sectorString.length() - 2);
            sectorString += ")";
            deleteStatement.setLong(1, instructor.getId());
            deleteStatement.setString(2, sectorString);
            int deleteBool = deleteStatement.executeUpdate();

            //insert rows into INSTRUCTOR_SECTOR table
            int insertBool = 0;
            ResultSet rs = selectStatement.executeQuery(); //not sure about that query
            List<String> specialization = new ArrayList<>(); //contains all sector names present in INSTRUCTOR_SECTOR
            while(rs.next()) {
                specialization.add(rs.getString("NAME"));
            }
            for (Sector s: sectors) {
                if(specialization.contains(s.name())) { //= if there's at least one row with that sector name,
                    //no matter the instructor, the if body executes
                    insertStatement.setString(1, s.name());
                    insertStatement.setLong(2, instructor.getId());
                    insertBool = insertStatement.executeUpdate();
                }
            }
            return (updateBool > 0 && (deleteBool > 0 || insertBool > 0)); //checks all changes applied
        } catch (SQLException e) {
            throw new RuntimeException(e); //maybe DataException?
        }
    }

    @Override
    public void clear() {

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
    public Instructor mapItem(ResultSet rs, List<String> enumList) throws SQLException {
        long id = rs.getLong("ID");
        String name = rs.getString("NAME");
        String lastName = rs.getString("LASTNAME");
        LocalDate dob = rs.getDate("DOB").toLocalDate();
        String email = rs.getString("EMAIL");
        List<Sector> sectors = new ArrayList<>();
        for (String s: enumList) {
            sectors.add(Sector.valueOf(s));
        }
        return new Instructor(id,name,lastName,dob,email,sectors);
    }
}


