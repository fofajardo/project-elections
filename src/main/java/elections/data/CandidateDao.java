package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class CandidateDao {
    /**
     * Inserts a new candidate record.
     * @param account an {@code Account} object from which to read the data 
     * @throws SQLException if a database access error occurs
     */
    public static void create(Candidate candidate) throws SQLException {
        String query = "INSERT INTO `candidates` ("
                + "`position_id`, `partylist_id`, "
                + "`first_name`, `middle_name`, "
                + "`last_name`, `suffix`"
                + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters that correspond to the query
            statement.setInt(1, candidate.getPositionId());
            statement.setInt(2, candidate.getPartyId());
            statement.setString(3, candidate.getFirstName());
            statement.setString(4, candidate.getMiddleName());
            statement.setString(5, candidate.getLastName());
            statement.setString(6, candidate.getSuffix());
            statement.executeUpdate();
        }
    }

    /**
     * Retrieves all candidate records.
     * @return a {@code List<Candidate>} collection containing {@code Candidate} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Candidate> findAll() throws SQLException {
        List<Candidate> itemList = new ArrayList<>();
        String query = "SELECT * FROM `candidates`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Candidate object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Candidate item = fromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Retrieves candidate records matching the given position ID.
     * @param positionId the position ID
     * @return a {@code List<Candidate>} collection containing {@code Candidate} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Candidate> findByPosition(
            int positionId) throws SQLException {
        List<Candidate> itemList = new ArrayList<>();
        String query = "SELECT * FROM `candidates`"
                + "    LEFT JOIN `parties` "
                + "        ON `candidates`.partylist_id = `parties`.id "
                + "    WHERE `position_id`=?"
                + "    ORDER BY `last_name`";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, positionId);
            // Iterate over the results, create a Candidate object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Candidate item = fromResultSet(results);
                    // Create and attach a Party object to the Candidate object
                    // which can be used to retrieve party details directly
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

    /**
     * Retrieves candidate records matching the given position ID,
     * including the number of votes cast for each candidate.
     * @param positionId the position ID
     * @param limit the limit number of results, use {@code 0}
     *        if there's no limit
     * @return a {@code List<Candidate>} collection containing {@code Candidate} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Candidate> findByPositionWithVotes(
            int positionId, int limit) throws SQLException {
        List<Candidate> itemList = new ArrayList<>();
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
            // Iterate over the results, create a Candidate object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Candidate item = fromResultSet(results);
                    // Create and attach a Party object to the Candidate object
                    // which can be used to retrieve party details directly
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
                    // Get number of votes from joined votes column
                    item.setVotes(results.getInt("votes"));
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Modifies an existing candidate record.
     * @param candidate an {@code Candidate} object from which to read the data 
     * @throws SQLException if a database access error occurs
     */
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
            // Set parameters that correspond to the query
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

    /**
     * Deletes a candidate record matching the given ID. 
     * @param id the candidate ID
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM `candidates` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }

    /**
     * Deletes a candidate record matching the ID of the given object. 
     * @param candidate the {@code Candidate} object
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Candidate candidate) throws SQLException {
        delete(candidate.getId());
    }

    /**
     * Creates a {@code Candidate} object using the given {@code ResultSet}.
     * @param results the {@code ResultSet} from which values will be retrieved
     * @return a {@code Candidate} object
     * @throws SQLException if a database access error occurs
     */
    private static Candidate fromResultSet(ResultSet results)
            throws SQLException {
        Candidate item = new Candidate();
        // Retrieve values from the designated columns
        item.setId(results.getInt(1));
        item.setPositionId(results.getInt(2));
        item.setPartyId(results.getInt(3));
        item.setFirstName(results.getString(4));
        item.setMiddleName(results.getString(5));
        item.setLastName(results.getString(6));
        item.setSuffix(results.getString(7));
        return item;
    }
}
