package main.school.data.abstractions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class IdSequenceRepo extends WithConnection {
    public long getId(String sequenceName) throws SQLException {
        String idFromSequence = String.format("SELECT %s.NEXTVAL() FROM DUAL", sequenceName);
        try(
                PreparedStatement ps = conn.prepareStatement(idFromSequence);
                ResultSet rs = ps.executeQuery();
        ) {
            rs.next();
            return rs.getLong(1);
        }
    }
}
