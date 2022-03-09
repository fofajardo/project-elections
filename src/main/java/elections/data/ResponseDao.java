package elections.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import elections.models.Candidate;
import elections.models.Party;
import elections.models.Response;

/**
 * This class defines static methods for accessing data related to responses.
 */
public class ResponseDao {
    /**
     * Inserts a record using data from the specified response.
     *
     * @param response the {@link Response} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void create(Response response) throws SQLException {
        String sql = "INSERT INTO `responses` ("
                + "    `voter_id`, `candidate_id`, `partylist_id`"
                + ") VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
            statement.setInt(1, response.getVoterId());
            // Interpret candidate or partylist identifier that is
            // set to zero (0) as SQL NULL
            int candidateId = response.getCandidateId();
            if (candidateId == 0) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, candidateId);
            }
            int partylistId = response.getPartylistId();
            if (partylistId == 0) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, partylistId);
            }
            statement.executeUpdate();
        }
    }

    /**
     * Returns a list containing all responses.
     *
     * @return a list containing {@link Response} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Response> findAll() throws SQLException {
        List<Response> itemList = new ArrayList<Response>();
        String sql = "SELECT * FROM `responses`";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Response object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(sql)) {
                while (results.next()) {
                    Response item = new Response();
                    item.setVoterId(results.getInt(1));
                    item.setCandidateId(results.getInt(2));
                    item.setPartylistId(results.getInt(3));
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Returns a list containing responses for candidates matching
     * the specified account identifier.
     *
     * @param accountId the account identifier
     * @return a list containing {@link Response} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Response> findByAccountAndCandidate(int accountId)
            throws SQLException {
        List<Response> itemList = new ArrayList<Response>();
        String sql = "SELECT * FROM `responses`"
                + "    INNER JOIN `candidates`"
                + "        ON `responses`.candidate_id = `candidates`.id"
                + "    LEFT JOIN `parties`"
                + "        ON `candidates`.partylist_id = `parties`.id"
                + "    WHERE `voter_id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            // Iterate over the results, create a Response object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Response item = new Response();
                    item.setVoterId(results.getInt(1));
                    item.setCandidateId(results.getInt(2));
                    // Create and attach a Candidate object to the Response
                    // object which can be used to retrieve candidate details
                    Candidate attachedCandidate = new Candidate();
                    attachedCandidate.setId(results.getInt(4));
                    attachedCandidate.setPositionId(results.getInt(5));
                    attachedCandidate.setPartyId(results.getInt(6));
                    attachedCandidate.setFirstName(results.getString(7));
                    attachedCandidate.setMiddleName(results.getString(8));
                    attachedCandidate.setLastName(results.getString(9));
                    attachedCandidate.setSuffix(results.getString(10));
                    item.setAttachedCandidate(attachedCandidate);
                    // Create and attach a Party object to the Candidate
                    // object which can be used to retrieve party details
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
            }
        }
        return itemList;
    }

    /**
     * Returns a list containing responses for party lists matching
     * the specified account identifier.
     *
     * @param accountId the account identifier
     * @return a list containing {@link Response} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Response> findByAccountAndPartylist(int accountId)
            throws SQLException {
        List<Response> itemList = new ArrayList<Response>();
        String sql = "SELECT * FROM `responses`"
                + "    INNER JOIN `parties`"
                + "        ON `responses`.partylist_id = `parties`.id"
                + "    WHERE `voter_id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            // Iterate over the results, create a Response object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Response item = new Response();
                    item.setVoterId(results.getInt(1));
                    item.setPartylistId(results.getInt(3));
                    // Create and attach a Party object to the Response object
                    // which can be used to retrieve party details
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
            }
        }
        return itemList;
    }

    /**
     * Updates a record using data from the specified response.
     *
     * @param response the {@link Response} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void update(Response response) throws SQLException {
        // Responses cannot be updated
        return;
    }

    /**
     * Deletes all responses matching the specified account identifier.
     *
     * @param accountId the account ID
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int accountId) throws SQLException {
        String sql = "DELETE FROM `responses` WHERE `voter_id` = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            statement.executeUpdate();
        }
    }

    /**
     * Deletes all responses matching the account identifier of
     * the specified response.
     *
     * @param response the {@link Response} object
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Response response) throws SQLException {
        delete(response.getVoterId());
    }

    /**
     * Deletes all responses.
     *
     * @throws SQLException if a database access error occurs
     */
    public static void deleteAll() throws SQLException {
        String query = "TRUNCATE TABLE `responses`";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }
}
