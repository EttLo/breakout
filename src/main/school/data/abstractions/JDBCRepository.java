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
    /*public List<T> queryForList(String sql, List<Object> typesToSet) throws DataException {
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
    }*/
    public abstract List<Object> variableForSecondQuery(ResultSet rset) throws DataException;
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