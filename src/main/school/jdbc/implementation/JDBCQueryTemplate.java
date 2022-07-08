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

    public Optional<T> findByName(String sql, String name) throws DataException {
        try (
                Connection con = getConn();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {

            stmt.setString(1, name); //
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



    public List<T> findByIdList(String sql, long id) throws DataException {
        List<T> items = new ArrayList<>();

        try (
                Connection con = getConn();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setLong(1, id);
            try(ResultSet rset = stmt.executeQuery(sql)){
                while (rset.next()) {
                    items.add(mapItem(rset));
                }
            }

        } catch (SQLException sqe) {
            sqe.printStackTrace();
            throw new DataException("Failed to retrieve data from database", sqe);
        }
        return items;
    }



    public abstract T mapItem(ResultSet rset) throws SQLException;
}
