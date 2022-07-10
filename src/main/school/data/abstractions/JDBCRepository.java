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

    //use for methods with two queries, the second being dependent on the output of the first
    public List<T> queriesForList(String sql, String sql2, List<Object> typesList) throws DataException {
        List<T> items = new ArrayList<>();
        try (
                PreparedStatement statement1 = conn.prepareStatement(sql);
                PreparedStatement statement2 = conn.prepareStatement(sql2);
        ) {
            setPreparedStatement(statement1, typesList);
            try (
                    ResultSet rs1 = statement1.executeQuery();
            ) {
                while (rs1.next()) {
                    List secondTypesList = variableForSecondQuery(rs1);
                    setPreparedStatement(statement2, secondTypesList);
                    List<String> enumList = new ArrayList<>();
                    try(
                            ResultSet rs2 = statement2.executeQuery();
                    ){
                        enumList.clear();
                        while(rs2.next()){
                            enumList.add(rs2.getString("NAME")); //abstract this
                        }
                        T temp = mapItem(rs1, enumList);
                        items.add(temp);
                    }
                }
            }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return items;
    }

    //use for methods with only one query
    /*
    public List<T> queryForList(String sql, List<Object> typesToSet) throws DataException {
        List<T> items = new ArrayList<>();
        try (
                PreparedStatement statement1 = conn.prepareStatement(sql);
        ) {
            setPreparedStatement(statement1, typesToSet);
            try (
                    ResultSet rset = statement1.executeQuery();
            ) {
                while (rset.next()) {
                        T temp = mapItem(rset,);
                        items.add(temp);
                    }
                }
            } catch (SQLException sqe) {
            sqe.printStackTrace();
        }
        return items;
    }

     */

    public void setPreparedStatement(PreparedStatement ps, List<Object> typesToSet) throws DataException {
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

    public abstract List<Object> variableForSecondQuery(ResultSet rs) throws DataException;
    public abstract T mapItem(ResultSet rs, List<String> enumList) throws SQLException;

}