package main.school.jdbc.implementation;

import main.school.data.DataException;
import main.school.data.abstractions.JdbcRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class JDBCQueryTemplate<T> extends JdbcRepository {

    public List<T> queryForList(String sql) throws DataException {
        List<T> items = new ArrayList<>();

        try (
                Connection con = getConn();
                Statement stmt = con.createStatement();
                ResultSet rset = stmt.executeQuery(sql);
        ) {
            while (rset.next()) {
                items.add(mapItem(rset));
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            throw new DataException("Failed to retrieve data from database", sqe);
        }
        return items;
    }

    public Optional<T> findById(String sql, long id) throws DataException {
        try (
                Connection con = getConn();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {

            stmt.setLong(1, id); //
            try (ResultSet rset = stmt.executeQuery(sql)) {
                if (rset.next()) {
                    return Optional.of(mapItem(rset));
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Failed to retrieve data from database", e);
        }
    }

    public boolean addOne(String sql, T t) throws DataException {
        List<T> items = new ArrayList<>();

        try (
                Connection con = getConn();
                PreparedStatement stmt = con.prepareStatement(sql);
//                ResultSet rset = stmt.executeQuery(sql);
        ) {
            //set shit
            assembleStatement(stmt, t);
            try (ResultSet rset = stmt.executeQuery(sql)) {
                while (rset.next()) {
                    items.add(mapItem(rset));
                }
            }

        } catch (SQLException sqe) {
            sqe.printStackTrace();
            throw new DataException("Failed to retrieve data from database", sqe);
        }
        return false;

    }

    public abstract T mapItem(ResultSet rset) throws SQLException;
    public abstract void assembleStatement(PreparedStatement stmt, T t) throws SQLException;
}
