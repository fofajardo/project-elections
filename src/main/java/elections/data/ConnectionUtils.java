/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

/**
 * This class provides a method to retrieve a connection to the MySQL database.
 * The database URL is formed using the constants defined in this class.
 */
public class ConnectionUtils {
    /**
     * The username of the MySQL account.
     */
    private static final String CONN_USERNAME = "root";
    /**
     * The password of the MySQL account.
     */
    private static final String CONN_PASSWORD = "";
    /**
     * The domain running the MySQL server.
     */
    private static final String DB_SERVER = "localhost";
    /**
     * The port of the MySQL server.
     */
    private static final int DB_PORT = 3306;
    /**
     * The name of the database in the MySQL server.
     */
    private static final String DB_NAME = "elections";

    /**
     * Returns a connection that can be used with a specific database.
     * This uses the constants defined in the {@link ConnectionUtils} class.
     * @return a {@link Connection} to the URL
     * @throws SQLException if a database access error occurs or the url is
     * {@code null}
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value specified by the {@code setLoginTimeout} method
     * has been exceeded and has at least tried to cancel the
     * current database connection attempt
     */
    public static Connection getConnection()
            throws SQLException, SQLTimeoutException {
        // Explicitly load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            return null;
        }

        // Attempt to establish and return a connection
        return DriverManager.getConnection(
                "jdbc:mysql://" + DB_SERVER + ":" + DB_PORT + "/" + DB_NAME,
                CONN_USERNAME,
                CONN_PASSWORD);
    }
}
