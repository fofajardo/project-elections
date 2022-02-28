package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class VoterDB {
    public static void create(Voter voter) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "INSERT INTO `voters` ("
                         + "`location_id`, `first_name`, `middle_name`, "
                         + "`last_name`, `voter_uuid`, `voter_hash`, "
                         + "`dt_last_signin`, `dt_vote_recorded`"
                         + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
    
            statement.setInt(1, voter.getLocationId());
            statement.setString(2, voter.getFirstName());
            statement.setString(3, voter.getMiddleName());
            statement.setString(4, voter.getLastName());
            statement.setString(5, voter.getVoterUuid());
            statement.setString(6, voter.getVoterHash());
            statement.setTimestamp(7, new Timestamp(voter.getLastSignIn().getTime()));
            statement.setTimestamp(8, new Timestamp(voter.getVoteRecorded().getTime()));

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static ArrayList<Voter> read() throws SQLException {
        ArrayList<Voter> itemList = new ArrayList<Voter>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `voters`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Voter item = new Voter();
                item.setId(results.getInt(1));
                item.setLocationId(results.getInt(2));
                item.setFirstName(results.getString(3));
                item.setMiddleName(results.getString(4));
                item.setLastName(results.getString(5));
                item.setVoterUuid(results.getString(6));
                item.setVoterHash(results.getString(7));
                item.setLastSignIn(results.getTimestamp(8));
                item.setVoteRecorded(results.getTimestamp(9));
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static Voter readId(int voterId) throws SQLException {
        Voter voter = null;
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `voters` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, voterId);
            ResultSet results = statement.executeQuery();
            results.first();
            voter = new Voter();
            voter.setId(results.getInt(1));
            voter.setLocationId(results.getInt(2));
            voter.setFirstName(results.getString(3));
            voter.setMiddleName(results.getString(4));
            voter.setLastName(results.getString(5));
            voter.setVoterUuid(results.getString(6));
            voter.setVoterHash(results.getString(7));
            voter.setLastSignIn(results.getTimestamp(8));
            voter.setVoteRecorded(results.getTimestamp(9));
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return voter;
    }

    public static void update(Voter voter) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "UPDATE `voters` SET"
                         + "    `location_id`=?,"
                         + "    `first_name`=?,"
                         + "    `middle_name`=?,"
                         + "    `last_name`=?,"
                         + "    `voter_uuid`=?,"
                         + "    `voter_hash`=?,"
                         + "    `dt_last_signin`=?,"
                         + "    `dt_vote_recorded`=?"
                         + "WHERE `id`=?";
            statement = connection.prepareStatement(query);

            statement.setInt(1, voter.getLocationId());
            statement.setString(2, voter.getFirstName());
            statement.setString(3, voter.getMiddleName());
            statement.setString(4, voter.getLastName());
            statement.setString(5, voter.getVoterUuid());
            statement.setString(6, voter.getVoterHash());
            statement.setTimestamp(7, new Timestamp(voter.getLastSignIn().getTime()));
            statement.setTimestamp(8, new Timestamp(voter.getVoteRecorded().getTime()));
            statement.setInt(9, voter.getId());

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

            String query = "DELETE FROM `voters` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(Voter voter) throws SQLException {
        delete(voter.getId());
    }
}
