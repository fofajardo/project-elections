package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class ResponseDB {
    public static void create(Response voteEntry) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "INSERT INTO `responses` "
                         + "(`voter_id`, `candidate_id`, `partylist_id`)"
                         + "VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, voteEntry.getVoterId());
            int candidateId = voteEntry.getCandidateId();
            if (candidateId == 0) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, candidateId);
            }
            int partylistId = voteEntry.getPartylistId();
            if (partylistId == 0) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, partylistId);
            }
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static ArrayList<Response> read() throws SQLException {
        ArrayList<Response> itemList = new ArrayList<Response>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `responses`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Response item = new Response();
                item.setVoterId(results.getInt(1));
                item.setCandidateId(results.getInt(2));
                item.setPartylistId(results.getInt(3));
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static ArrayList<Response> readFromVoter(int voterId) throws SQLException {
        ArrayList<Response> itemList = new ArrayList<Response>();
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `responses` WHERE `voter_id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, voterId);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Response item = new Response();
                item.setVoterId(results.getInt(1));
                item.setCandidateId(results.getInt(2));
                item.setPartylistId(results.getInt(3));
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

            String query = "SELECT COUNT(*) AS rowCount FROM `responses` WHERE `candidate_id` = ?";
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

    public static int getPartylistVoteCount(int partylistId) throws SQLException {
        PreparedStatement statement = null;
        int voteCount = -1;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT COUNT(*) AS rowCount FROM `responses` WHERE `partylist_id` = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, partylistId);
            ResultSet results = statement.executeQuery();

            voteCount = results.getInt(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return voteCount;
    }

    public static void update(Response voteEntry) throws SQLException {
        // Vote entries cannot be modified or updated
        return;
    }

    public static void delete(int voterId) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "DELETE FROM `responses` WHERE `voter_id` = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, voterId);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(Response entry) throws SQLException {
        delete(entry.getVoterId());
    }
}
