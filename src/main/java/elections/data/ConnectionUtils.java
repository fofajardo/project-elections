package elections.data;
import java.sql.*;

public class ConnectionUtils {
    private static final String CONN_USERNAME = "root";
    private static final String CONN_PASSWORD = "";
    private static final String DB_SERVER = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "elections";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            return null;
        }
        
        return DriverManager.getConnection(
                "jdbc:mysql://" + DB_SERVER + ":" + DB_PORT + "/" + DB_NAME,
                CONN_USERNAME,
                CONN_PASSWORD);
    }
}
