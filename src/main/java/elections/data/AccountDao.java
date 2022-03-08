package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class AccountDao {
    /**
     * Inserts a new account record.
     * @param account an {@code Account} object from which to read the data 
     * @throws SQLException if a database access error occurs
     */
    public static void create(Account account) throws SQLException {
        String query = "INSERT INTO `accounts` ("
                + "`uuid`, `first_name`, "
                + "`middle_name`, `last_name`, `suffix`, "
                + "`username`, `email`, `password`, "
                + "`dt_last_signin`, `dt_vote_recorded`, `role_id`"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters that correspond to the query
            statement.setString(1, account.getUuid());
            statement.setString(2, account.getFirstName());
            statement.setString(3, account.getMiddleName());
            statement.setString(4, account.getLastName());
            statement.setString(5, account.getSuffix());
            statement.setString(6, account.getUsername());
            statement.setString(7, account.getEmail());
            statement.setString(8, account.getPassword());
            // Don't convert last sign in date and vote recorded date
            // to SQL timestamp if it is null
            if (account.getLastSignIn() != null) {
                statement.setTimestamp(9, new Timestamp(account.getLastSignIn().getTime()));
            } else {
                statement.setTimestamp(9, null);
            }
            if (account.getVoteRecorded() != null) {
                statement.setTimestamp(10, new Timestamp(account.getVoteRecorded().getTime()));
            } else {
                statement.setTimestamp(10, null);
            }
            statement.setInt(11, account.getRoleId());
            statement.executeUpdate();
        }
    }

    /**
     * Retrieves all account records.
     * @return a {@code List<Account>} collection containing {@code Account} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Account> findAll() throws SQLException {
        List<Account> itemList = new ArrayList<>();
        String query = "SELECT * FROM `accounts`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            // Iterate over the results, create an Account object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Account item = fromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Retrieves an account record matching the given ID.
     * @param id the account ID
     * @return an {@code Account} object of the matching record
     * @throws SQLException if a database access error occurs
     */
    public static Account findById(int id) throws SQLException {
        Account account = null;
        String query = "SELECT * FROM `accounts` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            // Get the first result and create an Account object
            // based on the data
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = fromResultSet(results);
                }
            }
        }
        return account;
    }

    /**
     * Retrieves an account record matching the given UUID.
     * @param uuid the account UUID
     * @return an {@code Account} object of the matching record
     * @throws SQLException if a database access error occurs
     */
    public static Account findByUuid(String uuid) throws SQLException {
        Account account = null;
        String query = "SELECT * FROM `accounts` WHERE `uuid`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);
            // Get the first result and create an Account object
            // based on the data
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = fromResultSet(results);
                }
            }
        }
        return account;
    }

    /**
     * Retrieves an account record matching the given email or username
     * @param emailOrUsername the email or username associated with the account
     * @return an {@code Account} object of the matching record
     * @throws SQLException if a database access error occurs
     */
    public static Account findByEmailOrUsername(String emailOrUsername) throws SQLException {
        Account account = null;
        String query = "SELECT * FROM `accounts` WHERE `email`=? OR `username`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            // Get the first result and create an Account object
            // based on the data
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = fromResultSet(results);
                }
            }
        }
        return account;
    }

    /**
     * Modifies an existing account record.
     * @param account an {@code Account} object from which to read the data 
     * @throws SQLException if a database access error occurs
     */
    public static void update(Account account) throws SQLException {
        String query = "UPDATE `accounts` SET"
                + "    `uuid`=?,"
                + "    `first_name`=?,"
                + "    `middle_name`=?,"
                + "    `last_name`=?,"
                + "    `suffix`=?,"
                + "    `username`=?,"
                + "    `email`=?,"
                + "    `password`=?,"
                + "    `dt_last_signin`=?,"
                + "    `dt_vote_recorded`=?,"
                + "    `role_id`=?"
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters that correspond to the query
            statement.setString(1, account.getUuid());
            statement.setString(2, account.getFirstName());
            statement.setString(3, account.getMiddleName());
            statement.setString(4, account.getLastName());
            statement.setString(5, account.getSuffix());
            statement.setString(6, account.getUsername());
            statement.setString(7, account.getEmail());
            statement.setString(8, account.getPassword());
            // Don't convert last sign in date and vote recorded date
            // to SQL timestamp if it is null
            if (account.getLastSignIn() != null) {
                statement.setTimestamp(9, new Timestamp(account.getLastSignIn().getTime()));
            } else {
                statement.setTimestamp(9, null);
            }
            if (account.getVoteRecorded() != null) {
                statement.setTimestamp(10, new Timestamp(account.getVoteRecorded().getTime()));
            } else {
                statement.setTimestamp(10, null);
            }
            statement.setInt(11, account.getRoleId());
            statement.setInt(12, account.getId());
            statement.executeUpdate();
        }     
    }

    /**
     * Sets all vote recorded timestamps to SQL {@code NULL}.
     * @throws SQLException if a database access error occurs
     */
    public static void resetAllVoteCasted() throws SQLException {
        String query = "UPDATE `accounts` SET `dt_vote_recorded`=NULL";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    /**
     * Deletes an account record matching the given ID. 
     * @param id the account ID
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM `accounts` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    /**
     * Deletes an account record matching the ID of the given object. 
     * @param account the {@code Account} object
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Account account) throws SQLException {
        delete(account.getId());
    }

    /**
     * Creates an {@code Account} object using the given {@code ResultSet}.
     * @param results the {@code ResultSet} from which values will be retrieved
     * @return an {@code Account} object
     * @throws SQLException if a database access error occurs
     */
    private static Account fromResultSet(ResultSet results)
            throws SQLException {
        Account item = new Account();
        // Retrieve values from the designated columns
        item.setId(results.getInt(1));
        item.setUuid(results.getString(2));
        item.setFirstName(results.getString(3));
        item.setMiddleName(results.getString(4));
        item.setLastName(results.getString(5));
        item.setSuffix(results.getString(6));
        item.setUsername(results.getString(7));
        item.setEmail(results.getString(8));
        item.setPassword(results.getString(9));
        item.setLastSignIn(results.getTimestamp(10));
        item.setVoteRecorded(results.getTimestamp(11));
        item.setRoleId(results.getInt(12));
        return item;
    }
}
