package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class CandidateDB {
    public static void create(Candidate candidate) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "INSERT INTO `candidates` ("
                         + "`position_id`, `partylist_id`, `location_id`, "
                         + "`first_name`, `middle_name`, "
                         + "`last_name`, `suffix`"
                         + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
    
            statement.setInt(1, candidate.getPositionId());
            statement.setInt(2, candidate.getPartyId());
            statement.setInt(3, candidate.getLocationId());
            statement.setString(4, candidate.getFirstName());
            statement.setString(5, candidate.getMiddleName());
            statement.setString(6, candidate.getLastName());
            statement.setString(7, candidate.getSuffix());

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private static Candidate createFromResultSet(ResultSet results)
            throws SQLException {
        Candidate item = new Candidate();
        item.setId(results.getInt(1));
        item.setPositionId(results.getInt(2));
        item.setPartyId(results.getInt(3));
        item.setLocationId(results.getInt(4));
        item.setFirstName(results.getString(5));
        item.setMiddleName(results.getString(6));
        item.setLastName(results.getString(7));
        item.setSuffix(results.getString(8));
        return item;
    }
    
    public static ArrayList<Candidate> read() throws SQLException {
        ArrayList<Candidate> itemList = new ArrayList<Candidate>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `candidates`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Candidate item = createFromResultSet(results);
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static ArrayList<Candidate> readFromPosition(
            int positionId) throws SQLException {
        ArrayList<Candidate> itemList = new ArrayList<Candidate>();
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `candidates`"
                         + "    LEFT JOIN `parties` "
                         + "        ON `candidates`.partylist_id = `parties`.id "
                         + "    WHERE `position_id`=?"
                         + "    ORDER BY `last_name`";
            statement = connection.prepareStatement(query);
            statement.setInt(1, positionId);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Candidate item = createFromResultSet(results);

                int partyId = results.getInt(9);
                if (partyId > 0) {
                    Party attachedParty = new Party();
                    attachedParty.setId(partyId);
                    attachedParty.setCustomOrder(results.getInt(10));
                    attachedParty.setName(results.getString(11));
                    attachedParty.setAlias(results.getString(12));
                    attachedParty.setPartylist(results.getBoolean(13));
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

    public static ArrayList<Candidate> readFromPositionWithVotes(
            int positionId, int limit) throws SQLException {
        ArrayList<Candidate> itemList = new ArrayList<Candidate>();
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `candidates`"
                         + "    LEFT JOIN `parties`"
                         + "        ON `candidates`.partylist_id = `parties`.id"
                         + "    LEFT JOIN ("
                         + "        SELECT `candidate_id`, COUNT(`voter_id`) AS `votes`"
                         + "        FROM `responses`"
                         + "        WHERE `candidate_id` IS NOT NULL"
                         + "        GROUP BY `candidate_id`"
                         + "    ) candidateVotes"
                         + "        ON `candidates`.id = candidateVotes.candidate_id"
                         + "    WHERE `position_id`=?"
                         + "    ORDER BY votes DESC";
            if (limit > 0) {
                query += " LIMIT " + limit;
            }
            statement = connection.prepareStatement(query);
            statement.setInt(1, positionId);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Candidate item = createFromResultSet(results);

                int partyId = results.getInt(9);
                if (partyId > 0) {
                    Party attachedParty = new Party();
                    attachedParty.setId(partyId);
                    attachedParty.setCustomOrder(results.getInt(10));
                    attachedParty.setName(results.getString(11));
                    attachedParty.setAlias(results.getString(12));
                    attachedParty.setPartylist(results.getBoolean(13));
                    item.setAttachedParty(attachedParty);
                }

                item.setVotes(results.getInt("votes"));
                
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static void update(Candidate candidate) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "UPDATE `candidates` SET"
                         + "    `position_id`=?, "
                         + "    `partylist_id`=?, "
                         + "    `location_id`=?,"
                         + "    `first_name`=?,"
                         + "    `middle_name`=?,"
                         + "    `last_name`=?,"
                         + "    `suffix`=?"
                         + "    WHERE `id`=?";
            statement = connection.prepareStatement(query);
    
            statement.setInt(1, candidate.getPositionId());
            statement.setInt(2, candidate.getPartyId());
            statement.setInt(3, candidate.getLocationId());
            statement.setString(4, candidate.getFirstName());
            statement.setString(5, candidate.getMiddleName());
            statement.setString(6, candidate.getLastName());
            statement.setString(7, candidate.getSuffix());
            statement.setInt(8, candidate.getId());

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

            String query = "DELETE FROM `candidates` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(Candidate candidate) throws SQLException {
        delete(candidate.getId());
    }
}
