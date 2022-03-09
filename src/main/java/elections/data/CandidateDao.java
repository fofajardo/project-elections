package elections.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import elections.models.Candidate;
import elections.models.Party;

/**
 * This class defines static methods for accessing data related to candidates.
 */
public class CandidateDao {
    /**
     * Inserts a record using data from the specified candidate.
     *
     * @param candidate the {@link Candidate} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void create(Candidate candidate) throws SQLException {
        String sql = "INSERT INTO `candidates` ("
                + "    `position_id`, `partylist_id`,"
                + "    `first_name`, `middle_name`,"
                + "    `last_name`, `suffix`"
                + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
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
     * Returns a list containing all candidates.
     *
     * @return a list containing {@link Candidate} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Candidate> findAll() throws SQLException {
        List<Candidate> itemList = new ArrayList<>();
        String sql = "SELECT * FROM `candidates`";
        try (Connection connection = ConnectionUtils.getConnection();
                Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Candidate object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(sql)) {
                while (results.next()) {
                    Candidate item = modelFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Returns a list containing candidates matching the specified
     * position identifier.
     *
     * @param positionId the position identifier
     * @return a list containing {@link Candidate} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Candidate> findByPosition(int positionId)
            throws SQLException {
        List<Candidate> itemList = new ArrayList<>();
        String sql = "SELECT * FROM `candidates`"
                + "    LEFT JOIN `parties` "
                + "        ON `candidates`.partylist_id = `parties`.id "
                + "    WHERE `position_id`=?"
                + "    ORDER BY `last_name`";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, positionId);
            // Iterate over the results, create a Candidate object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Candidate item = modelFromResultSet(results);
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
     * Returns a list containing candidates matching the specified
     * position identifier, including the number of votes cast for
     * each candidate.
     *
     * @param positionId the position identifier
     * @param limit the limit to the number of {@link Candidate}
     *              objects returned by this method
     * @return a list containing {@link Candidate} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Candidate> findByPositionWithVotes(
            int positionId, int limit) throws SQLException {
        List<Candidate> itemList = new ArrayList<>();
        String sql = "SELECT * FROM `candidates`"
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
            sql += " LIMIT " + limit;
        }
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, positionId);
            // Iterate over the results, create a Candidate object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Candidate item = modelFromResultSet(results);
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
     * Updates a record using data from the specified candidate.
     *
     * @param candidate the {@link Candidate} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void update(Candidate candidate) throws SQLException {
        String sql = "UPDATE `candidates` SET"
                + "    `position_id`=?, "
                + "    `partylist_id`=?, "
                + "    `first_name`=?,"
                + "    `middle_name`=?,"
                + "    `last_name`=?,"
                + "    `suffix`=?"
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
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
     * Deletes a record matching the specified candidate identifier.
     *
     * @param candidateId the candidate identifier
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int candidateId) throws SQLException {
        String sql = "DELETE FROM `candidates` WHERE `id`=?";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, candidateId);
            statement.executeUpdate(sql);
        }
    }

    /**
     * Deletes a record matching the identifier of the specified candidate.
     *
     * @param candidate the {@link Candidate} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Candidate candidate) throws SQLException {
        delete(candidate.getId());
    }

    /**
     * Returns a candidate using data from the specified {@code ResultSet}.
     *
     * @param results the {@link ResultSet} whose values will be retrieved
     * @return a {@link Candidate} object
     * @throws SQLException if a database access error occurs
     */
    private static Candidate modelFromResultSet(ResultSet results)
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
