package main.school.jdbc.implementation;

import main.school.data.DataException;
import main.school.data.abstractions.JdbcRepository;
import main.school.model.WithID;

import java.sql.*;

public abstract class JDBCUpdateTemplate<T extends WithID> extends JdbcRepository {
    public abstract void assembleStatement(PreparedStatement stmt, T t) throws SQLException;

    public void addOne(String sql, String genIdSQL, T t) throws DataException {
        try (
                Connection con = getConn();
                Statement st = conn.createStatement();
                ResultSet kr = st.executeQuery(genIdSQL);
                PreparedStatement stmt = con.prepareStatement(sql);
//                ResultSet rset = stmt.executeQuery(sql);
        ) {
            //set shit
            kr.next();
            long id = kr.getLong(1);
            t.setId(id);
            assembleStatement(stmt, t);
            stmt.executeQuery(sql);

        } catch (SQLException sqe) {
            sqe.printStackTrace();
            throw new DataException("Failed to retrieve data from database", sqe);
        }
    }
}