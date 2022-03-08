package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class CandidateDB {
    private static Candidate createFromResultSet(ResultSet results)
            throws SQLException {
        Candidate item = new Candidate();
        item.setId(results.getInt(1));
        item.setPositionId(results.getInt(2));
        item.setPartyId(results.getInt(3));
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

        return item;
    }
    
    public static void create(Candidate candidate) throws SQLException {
        String query = "INSERT INTO `candidates` ("
                + "`position_id`, `partylist_id`, "
                + "`first_name`, `middle_name`, "
                + "`last_name`, `suffix`"
                + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, candidate.getPositionId());
            statement.setInt(2, candidate.getPartyId());
            statement.setString(3, candidate.getFirstName());
            statement.setString(4, candidate.getMiddleName());
            statement.setString(5, candidate.getLastName());
            statement.setString(6, candidate.getSuffix());

            statement.executeUpdate();
        }
    }

    public static ArrayList<Candidate> read() throws SQLException {
        ArrayList<Candidate> itemList = new ArrayList<Candidate>();
        String query = "SELECT * FROM `candidates`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Candidate item = createFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public static ArrayList<Candidate> readFromPosition(
            int positionId) throws SQLException {
        ArrayList<Candidate> itemList = new ArrayList<Candidate>();
        String query = "SELECT * FROM `candidates`"
                + "    LEFT JOIN `parties` "
                + "        ON `candidates`.partylist_id = `parties`.id "
                + "    WHERE `position_id`=?"
                + "    ORDER BY `last_name`";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, positionId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Candidate item = createFromResultSet(results);
    
                    int partyId = results.getInt(8);
                    if (partyId > 0) {
                        Party attachedParty = new Party();
                        attachedParty.setId(partyId);
                        attachedParty.setCustomOrder(results.getInt(9));
                        attachedParty.setName(results.getString(10));
                        attachedParty.setAlias(results.getString(11));
                        attachedParty.setPartylist(results.getBoolean(12));
                        item.setAttachedParty(attachedParty);
                    }
    
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public static ArrayList<Candidate> readFromPositionWithVotes(
            int positionId, int limit) throws SQLException {
        ArrayList<Candidate> itemList = new ArrayList<Candidate>();
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
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, positionId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Candidate item = createFromResultSet(results);
    
                    int partyId = results.getInt(8);
                    if (partyId > 0) {
                        Party attachedParty = new Party();
                        attachedParty.setId(partyId);
                        attachedParty.setCustomOrder(results.getInt(9));
                        attachedParty.setName(results.getString(10));
                        attachedParty.setAlias(results.getString(11));
                        attachedParty.setPartylist(results.getBoolean(12));
                        item.setAttachedParty(attachedParty);
                    }
    
                    item.setVotes(results.getInt("votes"));
                    
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }
    
    public static void update(Candidate candidate) throws SQLException {
        String query = "UPDATE `candidates` SET"
                + "    `position_id`=?, "
                + "    `partylist_id`=?, "
                + "    `first_name`=?,"
                + "    `middle_name`=?,"
                + "    `last_name`=?,"
                + "    `suffix`=?"
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, candidate.getPositionId());
            statement.setInt(2, candidate.getPartyId());
            statement.setString(3, candidate.getFirstName());
            statement.setString(4, candidate.getMiddleName());
            statement.setString(5, candidate.getLastName());
            statement.setString(6, candidate.getSuffix());
            statement.setInt(7, candidate.getId());

            statement.executeUpdate();
        }    
    }

    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM `candidates` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }

    public static void delete(Candidate candidate) throws SQLException {
        delete(candidate.getId());
    }
}
