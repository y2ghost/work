package study.ywork.doc.hsqldb;

import java.sql.DriverManager;
import java.sql.SQLException;

public class HsqldbConnection extends DatabaseConnection {
    public HsqldbConnection(String fileNamePrefix)
            throws SQLException {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("HSQLDB database driver not found");
        }
        connection = DriverManager.getConnection(
                "jdbc:hsqldb:src/main/resources/db/" + fileNamePrefix + ";shutdown=true", "SA", "");
    }
}
