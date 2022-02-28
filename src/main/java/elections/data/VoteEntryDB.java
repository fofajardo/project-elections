package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class VoteEntryDB {
    public static void create(VoteEntry voteEntry) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "INSERT INTO `vote_entries` "
                         + "(`voter_id`, `candidate_id`)"
                         + "VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
    
            statement.setInt(1, voteEntry.getVoterId());
            statement.setInt(2, voteEntry.getCandidateId());

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static ArrayList<VoteEntry> read() throws SQLException {
        ArrayList<VoteEntry> itemList = new ArrayList<VoteEntry>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `vote_entries`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                VoteEntry item = new VoteEntry();
                item.setVoterId(results.getInt(1));
                item.setCandidateId(results.getInt(2));
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static ArrayList<VoteEntry> readFromVoter(int voterId) throws SQLException {
        ArrayList<VoteEntry> itemList = new ArrayList<VoteEntry>();
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `vote_entries` WHERE `voter_id` = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, voterId);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                VoteEntry item = new VoteEntry();
                item.setVoterId(results.getInt(1));
                item.setCandidateId(results.getInt(2));
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static int getCandidateVoteCount(int candidateId) throws SQLException {
        PreparedStatement statement = null;
        int voteCount = -1;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT COUNT(*) AS rowCount FROM `vote_entries` WHERE `candidate_id` = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, candidateId);
            ResultSet results = statement.executeQuery();

            voteCount = results.getInt(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return voteCount;
    }

    public static void update(VoteEntry voteEntry) throws SQLException {
        // Vote entries cannot be updated
        return;
    }

    public static void delete(int voterId) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "DELETE FROM `vote_entries` WHERE `voter_id` = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, voterId);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(VoteEntry entry) throws SQLException {
        delete(entry.getVoterId());
    }
}
