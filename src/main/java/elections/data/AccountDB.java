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
                         + "`uuid`, `location_id`, `first_name`, "
                         + "`middle_name`, `last_name`, `suffix`, "
                         + "`username`, `email`, `password`, "
                         + "`dt_last_signin`, `dt_vote_recorded`"
                         + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
    
            statement.setString(1, account.getUuid());
            statement.setInt(2, account.getLocationId());
            statement.setString(3, account.getFirstName());
            statement.setString(4, account.getMiddleName());
            statement.setString(5, account.getLastName());
            statement.setString(6, account.getSuffix());
            statement.setString(7, account.getUsername());
            statement.setString(8, account.getEmail());
            statement.setString(9, account.getPassword());
            if (account.getLastSignIn() != null) {
                statement.setTimestamp(10, new Timestamp(account.getLastSignIn().getTime()));
            } else {
                statement.setTimestamp(10, null);
            }
            if (account.getVoteRecorded() != null) {
                statement.setTimestamp(11, new Timestamp(account.getVoteRecorded().getTime()));
            } else {
                statement.setTimestamp(11, null);
            }

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
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
                Account item = new Account();
                item.setId(results.getInt(1));
                item.setUuid(results.getString(2));
                item.setLocationId(results.getInt(3));
                item.setFirstName(results.getString(4));
                Object middleName = results.getObject(5);
                if (middleName == null) {
                    item.setMiddleName("");
                } else {
                    item.setMiddleName(String.valueOf(middleName));
                }
                item.setLastName(results.getString(6));
                Object suffix = results.getObject(7);
                if (suffix == null) {
                    item.setSuffix("");
                } else {
                    item.setSuffix(String.valueOf(suffix));
                }
                item.setUsername(results.getString(8));
                item.setEmail(results.getString(9));
                item.setPassword(results.getString(10));
                item.setLastSignIn(results.getTimestamp(11));
                item.setVoteRecorded(results.getTimestamp(12));
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
                account = new Account();
                account.setId(results.getInt(1));
                account.setUuid(results.getString(2));
                account.setLocationId(results.getInt(3));
                account.setFirstName(results.getString(4));
                account.setMiddleName(results.getString(5));
                account.setLastName(results.getString(6));
                account.setSuffix(results.getString(7));
                account.setUsername(results.getString(8));
                account.setEmail(results.getString(9));
                account.setPassword(results.getString(10));
                account.setLastSignIn(results.getTimestamp(11));
                account.setVoteRecorded(results.getTimestamp(12));
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
                account = new Account();
                account.setId(results.getInt(1));
                account.setUuid(results.getString(2));
                account.setLocationId(results.getInt(3));
                account.setFirstName(results.getString(4));
                account.setMiddleName(results.getString(5));
                account.setLastName(results.getString(6));
                account.setSuffix(results.getString(7));
                account.setUsername(results.getString(8));
                account.setEmail(results.getString(9));
                account.setPassword(results.getString(10));
                account.setLastSignIn(results.getTimestamp(11));
                account.setVoteRecorded(results.getTimestamp(12));
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
                account = new Account();
                account.setId(results.getInt(1));
                account.setUuid(results.getString(2));
                account.setLocationId(results.getInt(3));
                account.setFirstName(results.getString(4));
                account.setMiddleName(results.getString(5));
                account.setLastName(results.getString(6));
                account.setSuffix(results.getString(7));
                account.setUsername(results.getString(8));
                account.setEmail(results.getString(9));
                account.setPassword(results.getString(10));
                account.setLastSignIn(results.getTimestamp(11));
                account.setVoteRecorded(results.getTimestamp(12));
                
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
                         + "    `location_id`=?,"
                         + "    `first_name`=?,"
                         + "    `middle_name`=?,"
                         + "    `last_name`=?,"
                         + "    `suffix`=?,"
                         + "    `username`=?,"
                         + "    `email`=?,"
                         + "    `password`=?,"
                         + "    `dt_last_signin`=?,"
                         + "    `dt_vote_recorded`=?"
                         + "    WHERE `id`=?";
            statement = connection.prepareStatement(query);

            statement.setString(1, account.getUuid());
            statement.setInt(2, account.getLocationId());
            statement.setString(3, account.getFirstName());
            statement.setString(4, account.getMiddleName());
            statement.setString(5, account.getLastName());
            statement.setString(6, account.getSuffix());
            statement.setString(7, account.getUsername());
            statement.setString(8, account.getEmail());
            statement.setString(9, account.getPassword());
            if (account.getLastSignIn() != null) {
                statement.setTimestamp(10, new Timestamp(account.getLastSignIn().getTime()));
            } else {
                statement.setTimestamp(10, null);
            }
            if (account.getVoteRecorded() != null) {
                statement.setTimestamp(11, new Timestamp(account.getVoteRecorded().getTime()));
            } else {
                statement.setTimestamp(11, null);
            }
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
