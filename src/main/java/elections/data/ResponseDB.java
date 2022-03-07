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

    public static ArrayList<Response> readFromVoterByCandidate(int voterId) throws SQLException {
        ArrayList<Response> itemList = new ArrayList<Response>();
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `responses`"
                         + "    INNER JOIN `candidates`"
                         + "        ON `responses`.candidate_id = `candidates`.id"
                         + "    LEFT JOIN `parties`"
                         + "        ON `candidates`.partylist_id = `parties`.id"
                         + "    WHERE `voter_id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, voterId);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Response item = new Response();
                item.setVoterId(results.getInt(1));
                item.setCandidateId(results.getInt(2));

                Candidate attachedCandidate = new Candidate();
                attachedCandidate.setId(results.getInt(4));
                attachedCandidate.setPositionId(results.getInt(5));
                attachedCandidate.setPartyId(results.getInt(6));
                attachedCandidate.setFirstName(results.getString(7));
                attachedCandidate.setMiddleName(results.getString(8));
                attachedCandidate.setLastName(results.getString(9));
                attachedCandidate.setSuffix(results.getString(10));
                item.setAttachedCandidate(attachedCandidate);

                int partyId = results.getInt(11);
                if (partyId > 0) {
                    Party attachedParty = new Party();
                    attachedParty.setId(partyId);
                    attachedParty.setCustomOrder(results.getInt(12));
                    attachedParty.setName(results.getString(13));
                    attachedParty.setAlias(results.getString(14));
                    attachedParty.setPartylist(results.getBoolean(15));
                    attachedCandidate.setAttachedParty(attachedParty);
                }

                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static ArrayList<Response> readFromVoterByPartylist(int voterId) throws SQLException {
        ArrayList<Response> itemList = new ArrayList<Response>();
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `responses`"
                         + "    INNER JOIN `parties`"
                         + "        ON `responses`.partylist_id = `parties`.id"
                         + "    WHERE `voter_id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, voterId);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Response item = new Response();
                item.setVoterId(results.getInt(1));
                item.setPartylistId(results.getInt(3));

                int partyId = results.getInt(4);
                if (partyId > 0) {
                    Party attachedParty = new Party();
                    attachedParty.setId(partyId);
                    attachedParty.setCustomOrder(results.getInt(5));
                    attachedParty.setName(results.getString(6));
                    attachedParty.setAlias(results.getString(7));
                    attachedParty.setPartylist(results.getBoolean(8));
                    item.setAttachedParty(attachedParty);
                }

                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
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
