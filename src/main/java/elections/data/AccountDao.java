package elections.data;

import java.sql.*;
import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;

import elections.models.*;

public class AccountDao {
    private static Account createFromResultSet(ResultSet results)
            throws SQLException {
        Account item = new Account();
        item.setId(results.getInt(1));
        item.setUuid(results.getString(2));
        item.setFirstName(results.getString(3));
        Object middleName = results.getObject(4);
        if (middleName == null) {
            item.setMiddleName("");
        } else {
            item.setMiddleName(String.valueOf(middleName));
        }
        item.setLastName(results.getString(5));
        Object suffix = results.getObject(6);
        if (suffix == null) {
            item.setSuffix("");
        } else {
            item.setSuffix(String.valueOf(suffix));
        }
        item.setUsername(results.getString(7));
        item.setEmail(results.getString(8));
        item.setPassword(results.getString(9));
        item.setLastSignIn(results.getTimestamp(10));
        item.setVoteRecorded(results.getTimestamp(11));
        item.setRoleId(results.getInt(12));
        return item;
    }

    public static void create(Account account) throws SQLException {
        String query = "INSERT INTO `accounts` ("
                + "`uuid`, `first_name`, "
                + "`middle_name`, `last_name`, `suffix`, "
                + "`username`, `email`, `password`, "
                + "`dt_last_signin`, `dt_vote_recorded`, `role_id`"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, account.getUuid());
            statement.setString(2, account.getFirstName());
            statement.setString(3, account.getMiddleName());
            statement.setString(4, account.getLastName());
            statement.setString(5, account.getSuffix());
            statement.setString(6, account.getUsername());
            statement.setString(7, account.getEmail());
            statement.setString(8, account.getPassword());
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


    public static ArrayList<Account> read() throws SQLException {
        ArrayList<Account> itemList = new ArrayList<Account>();
        String query = "SELECT * FROM `accounts`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Account item = createFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public static Account readId(int id) throws SQLException {
        Account account = null;
        String query = "SELECT * FROM `accounts` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = createFromResultSet(results);
                }
            }
        }
        return account;
    }

    public static Account readUuid(String uuid) throws SQLException {
        Account account = null;
        String query = "SELECT * FROM `accounts` WHERE `uuid`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = createFromResultSet(results);
                }
            }
        }
        return account;
    }

    public static Account readCredentials(String emailOrUsername, String password) throws SQLException {
        Account account = null;
        String query = "SELECT * FROM `accounts` WHERE `email`=? OR `username`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    account = createFromResultSet(results);
                    
                    String hashedPassword = DigestUtils.sha256Hex(password);
                    if (!account.getPassword().equals(hashedPassword)) {
                        account = null;
                    }
                }
            }
        }
        return account;
    }
    
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
            statement.setString(1, account.getUuid());
            statement.setString(2, account.getFirstName());
            statement.setString(3, account.getMiddleName());
            statement.setString(4, account.getLastName());
            statement.setString(5, account.getSuffix());
            statement.setString(6, account.getUsername());
            statement.setString(7, account.getEmail());
            statement.setString(8, account.getPassword());
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

    public static void clearVoteState() throws SQLException {
        String query = "UPDATE `accounts` SET `dt_vote_recorded`=NULL";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }
    
    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM `accounts` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public static void delete(Account voter) throws SQLException {
        delete(voter.getId());
    }
}
