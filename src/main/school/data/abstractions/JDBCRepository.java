package main.school.data.abstractions;

import main.school.data.DataException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class JDBCRepository<T> {
    protected Connection conn;


    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }


  /*  try(
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
            */

    public List<T> queryForList(String sql, String sql2, List<Object> typesToSet) throws DataException {
        List<T> items = new ArrayList<>();
        try (
                PreparedStatement statement1 = conn.prepareStatement(sql);
                PreparedStatement statement2 = conn.prepareStatement(sql2);
        ) {
            setPreparedStatement(statement1, typesToSet);
            try (
                    ResultSet rset = statement1.executeQuery();
            ) {
                while (rset.next()) {
                    List secondTypeToSet = variableForSecondQuery(rset);
                    setPreparedStatement(statement2, secondTypeToSet);
                    List<String> enumList = new ArrayList<>();
                    try(
                            ResultSet renum = statement2.executeQuery();
                    ){
                        enumList.clear();
                        while(renum.next()){
                            enumList.add(renum.getString("NAME"));
                        }
                        T temp = mapItem(rset, enumList);
                        items.add(temp);
                    }
                }
            }
            } catch (SQLException sqe) {
                sqe.printStackTrace();
            }
            return items;
        }
    public abstract List<Object> variableForSecondQuery(ResultSet rset);
    public void setPreparedStatement(PreparedStatement ps, List<Object> typesToSet)throws DataException {
        int i = 0;
        for (Object o: typesToSet) {
            try {
                ps.setObject(i, o);
                i++;
            } catch (SQLException e) {
                throw new DataException(e.getMessage(), e);
            }

        }
    }
    public abstract T mapItem(ResultSet rset, List<? extends String> enumList) throws SQLException;

}