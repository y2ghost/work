package study.ywork.doc.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseConnection {
    protected Connection connection;

    public void close() throws SQLException {
        connection.close();
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public PreparedStatement createPreparedStatement(String query)
            throws SQLException {
        return connection.prepareStatement(query);
    }

    public void update(String expression) throws SQLException {
        int result = 0;
        try (Statement st = createStatement()) {
            result = st.executeUpdate(expression);
        }

        if (result < 0) {
            throw new SQLException("db error : " + expression);
        }
    }
}
