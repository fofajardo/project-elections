/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import elections.models.Account;

/**
 * This class defines static methods for accessing data related to accounts.
 */
public class AccountDao {
    /**
     * Inserts a record using data from the specified account.
     *
     * @param account the {@link Account} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void create(Account account) throws SQLException {
        String sql = "INSERT INTO `accounts` ("
                + "    `uuid`, `first_name`, `middle_name`,"
                + "    `last_name`, `suffix`, `username`,"
                + "    `email`, `password`, `dt_last_signin`,"
                + "    `dt_vote_recorded`, `role_id`"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
            statement.setString(1, account.getUuid());
            statement.setString(2, account.getFirstName());
            statement.setString(3, account.getMiddleName());
            statement.setString(4, account.getLastName());
            statement.setString(5, account.getSuffix());
            statement.setString(6, account.getUsername());
            statement.setString(7, account.getEmail());
            statement.setString(8, account.getPassword());
            // Don't convert the last sign in date and the vote recorded
            // date to SQL timestamp if it is null
            Timestamp lastSignIn = null;
            if (account.getLastSignIn() != null) {
                lastSignIn = new Timestamp(
                        account.getLastSignIn().getTime());
            }
            Timestamp voteRecorded = null;
            if (account.getVoteRecorded() != null) {
                voteRecorded = new Timestamp(
                        account.getVoteRecorded().getTime());
            }
            statement.setTimestamp(9, lastSignIn);
            statement.setTimestamp(10, voteRecorded);
            statement.setInt(11, account.getRoleId());
            statement.executeUpdate();
        }
    }

    /**
     * Returns a list containing all accounts.
     *
     * @return a list containing {@link Account} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Account> findAll() throws SQLException {
        List<Account> itemList = new ArrayList<>();
        String sql = "SELECT * FROM `accounts`";
        try (Connection connection = ConnectionUtils.getConnection();
                Statement statement = connection.createStatement()) {
            // Iterate over the results, create an Account object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(sql)) {
                while (results.next()) {
                    Account item = modelFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Returns an account matching the specified account identifier.
     *
     * @param accountId the account identifier
     * @return an {@link Account} object
     * @throws SQLException if a database access error occurs
     */
    public static Account findById(int accountId) throws SQLException {
        Account account = null;
        String sql = "SELECT * FROM `accounts` WHERE `id`=?";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            // Get the first result and create an Account object
            // based on the data
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = modelFromResultSet(results);
                }
            }
        }
        return account;
    }

    /**
     * Returns an account matching the specified UUID.
     *
     * @param uuid the account UUID
     * @return an {@link Account} object
     * @throws SQLException if a database access error occurs
     */
    public static Account findByUuid(String uuid) throws SQLException {
        Account account = null;
        String sql = "SELECT * FROM `accounts` WHERE `uuid`=?";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            // Get the first result and create an Account object
            // based on the data
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = modelFromResultSet(results);
                }
            }
        }
        return account;
    }

    /**
     * Returns an account matching the specified email or username.
     *
     * @param emailOrUsername the email or username associated with the account
     * @return an {@link Account} object
     * @throws SQLException if a database access error occurs
     */
    public static Account findByEmailOrUsername(String emailOrUsername)
            throws SQLException {
        Account account = null;
        String sql = "SELECT * FROM `accounts` WHERE `email`=? OR `username`=?";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            // Get the first result and create an Account object
            // based on the data
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = modelFromResultSet(results);
                }
            }
        }
        return account;
    }

    /**
     * Updates a record using data from the specified account.
     *
     * @param account the {@link Account} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void update(Account account) throws SQLException {
        String sql = "UPDATE `accounts` SET"
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
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
            statement.setString(1, account.getUuid());
            statement.setString(2, account.getFirstName());
            statement.setString(3, account.getMiddleName());
            statement.setString(4, account.getLastName());
            statement.setString(5, account.getSuffix());
            statement.setString(6, account.getUsername());
            statement.setString(7, account.getEmail());
            statement.setString(8, account.getPassword());
            // Don't convert the last sign in date and the vote recorded
            // date to SQL timestamp if it is null
            Timestamp lastSignIn = null;
            if (account.getLastSignIn() != null) {
                lastSignIn = new Timestamp(
                        account.getLastSignIn().getTime());
            }
            Timestamp voteRecorded = null;
            if (account.getVoteRecorded() != null) {
                voteRecorded = new Timestamp(
                        account.getVoteRecorded().getTime());
            }
            statement.setTimestamp(9, lastSignIn);
            statement.setTimestamp(10, voteRecorded);
            statement.setInt(11, account.getRoleId());
            statement.setInt(12, account.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Updates all vote recorded timestamps to SQL {@code NULL}.
     *
     * @throws SQLException if a database access error occurs
     */
    public static void resetAllVoteRecorded() throws SQLException {
        String sql = "UPDATE `accounts` SET `dt_vote_recorded`=NULL";
        try (Connection connection = ConnectionUtils.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    /**
     * Deletes a record matching the specified account identifier.
     *
     * @param id the account identifier
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int id) throws SQLException {
        String sql = "DELETE FROM `accounts` WHERE `id`=?";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    /**
     * Deletes a record matching the identifier of the specified account.
     *
     * @param account the {@link Account} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Account account) throws SQLException {
        delete(account.getId());
    }

    /**
     * Returns an account using data from the specified {@code ResultSet}.
     *
     * @param results the {@link ResultSet} whose values will be retrieved
     * @return an {@link Account} object
     * @throws SQLException if a database access error occurs
     */
    private static Account modelFromResultSet(ResultSet results)
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
