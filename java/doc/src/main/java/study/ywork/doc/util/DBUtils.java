package study.ywork.doc.util;

import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.hsqldb.HsqldbConnection;

import java.sql.SQLException;

public class DBUtils {
    private static final String DB_FILE_NAME = "filmfestival";

    public static DatabaseConnection getFilmDBConnection() throws SQLException {
        return new HsqldbConnection(DB_FILE_NAME);
    }

    private DBUtils() {
    }
}
