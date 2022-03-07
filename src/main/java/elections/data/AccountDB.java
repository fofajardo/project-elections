package elections.data;

import java.sql.*;
import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;

import elections.models.*;

public class AccountDB {
    public static void create(Account account) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "INSERT INTO `accounts` ("
                         + "`uuid`, `first_name`, "
                         + "`middle_name`, `last_name`, `suffix`, "
                         + "`username`, `email`, `password`, "
                         + "`dt_last_signin`, `dt_vote_recorded`, `role_id`"
                         + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);

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
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

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

    public static ArrayList<Account> read() throws SQLException {
        ArrayList<Account> itemList = new ArrayList<Account>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `accounts`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Account item = createFromResultSet(results);
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static Account readId(int id) throws SQLException {
        Account account = null;
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `accounts` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                account = createFromResultSet(results);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return account;
    }

    public static Account readUuid(String uuid) throws SQLException {
        Account account = null;
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `accounts` WHERE `uuid`=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                account = createFromResultSet(results);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return account;
    }

    public static Account readCredentials(String emailOrUsername, String password) throws SQLException {
        Account account = null;
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `accounts` WHERE `email`=? OR `username`=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                account = createFromResultSet(results);
                
                String hashedPassword = DigestUtils.sha256Hex(password);
                if (!account.getPassword().equals(hashedPassword)) {
                    account = null;
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return account;
    }
    
    public static void update(Account account) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

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
                         + "    `dt_vote_recorded`=?"
                         + "    `role_id`=?"
                         + "    WHERE `id`=?";
            statement = connection.prepareStatement(query);

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
        } finally {
            if (statement != null) {
                statement.close();
            }
        }        
    }

    public static void delete(int id) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "DELETE FROM `accounts` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(Account voter) throws SQLException {
        delete(voter.getId());
    }
}
