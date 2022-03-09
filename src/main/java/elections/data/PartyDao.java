package elections.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import elections.models.Party;

/**
 * This class defines static methods for accessing data related to parties.
 */
public class PartyDao {
    /**
     * Inserts a record using data from the specified party.
     *
     * @param party the {@link Party} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void create(Party party) throws SQLException {
        String sql = "INSERT INTO `parties` ("
                + "    `custom_order`, `party_name`,"
                + "    `party_alias`, `is_partylist`"
                + ") VALUES (?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
            statement.setInt(1, party.getCustomOrder());
            statement.setString(2, party.getName());
            statement.setString(3, party.getAlias());
            statement.setBoolean(4, party.isPartylist());
            statement.executeUpdate();
        }
    }

    /**
     * Returns a list containing all parties.
     *
     * @return a list containing {@link Party} objects
     * @throws SQLException
     */
    public static List<Party> findAll() throws SQLException {
        List<Party> itemList = new ArrayList<Party>();
        String sql = "SELECT * FROM `parties`";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Party object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(sql)) {
                while (results.next()) {
                    Party item = modelFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Returns a party matching the specified party identifier.
     *
     * @param id the party identifier
     * @return a {@link Party} object
     * @throws SQLException if a database access error occurs
     */
    public static Party findById(int partyId) throws SQLException {
        Party item = null;
        String sql = "SELECT * FROM `parties` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, partyId);
            // Get the first result and create a Party object
            // based on the data
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    item = modelFromResultSet(results);
                }
            }
        }
        return item;
    }

    /**
     * Returns a list containing parties that are classified as a party list.
     *
     * @return a list containing {@link Party} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Party> findByPartylist() throws SQLException {
        List<Party> itemList = new ArrayList<Party>();
        String sql = "SELECT * FROM `parties`"
                + "    WHERE `is_partylist`=1"
                + "    ORDER BY `custom_order`";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Party object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(sql)) {
                while (results.next()) {
                    Party item = modelFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Returns a list containing parties that are classified as a party list,
     * including the number of votes cast for each party list.
     *
     * @param limit the limit to the number of {@link Party}
     *              objects returned by this method
     * @return a list containing {@link Party} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Party> findByPartylistWithVotes(int limit)
            throws SQLException {
        List<Party> itemList = new ArrayList<Party>();
        String sql = "SELECT * FROM `parties`"
                + "    LEFT JOIN ("
                + "        SELECT `partylist_id`, COUNT(`voter_id`) AS `votes`"
                + "        FROM `responses`"
                + "        WHERE `partylist_id` IS NOT NULL"
                + "        GROUP BY `partylist_id`"
                + "    ) partylistVotes"
                + "        ON `parties`.id = partylistVotes.partylist_id"
                + "    WHERE `is_partylist`=1"
                + "    ORDER BY votes DESC";
        if (limit > 0) {
            sql += " LIMIT " + limit;
        }
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Party object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(sql)) {
                while (results.next()) {
                    Party item = modelFromResultSet(results);
                    // Get number of votes from joined votes column
                    item.setVotes(results.getInt("votes"));
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Updates a record using data from the specified party.
     *
     * @param party the {@link Party} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void update(Party party) throws SQLException {
        String sql = "UPDATE `parties` SET"
                + "    `custom_order`=?, "
                + "    `party_name`=?, "
                + "    `party_alias`=?, "
                + "    `is_partylist`=? "
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
            statement.setInt(1, party.getCustomOrder());
            statement.setString(2, party.getName());
            statement.setString(3, party.getAlias());
            statement.setBoolean(4, party.isPartylist());
            statement.setInt(5, party.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Deletes a record matching the specified party identifier.
     *
     * @param partyId the party identifier
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int partyId) throws SQLException {
        String sql = "DELETE FROM `parties` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, partyId);
            statement.executeUpdate(sql);
        }
    }

    /**
     * Deletes a record matching the identifier of the specified party.
     *
     * @param party the {@link Party} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Party party) throws SQLException {
        delete(party.getId());
    }

    /**
     * Returns a party using data from the specified {@code ResultSet}.
     *
     * @param results the {@link ResultSet} whose values will be retrieved
     * @return a {@link Party} object
     * @throws SQLException if a database access error occurs
     */
    private static Party modelFromResultSet(ResultSet results)
            throws SQLException {
        Party item = new Party();
        // Retrieve values from the designated columns
        item.setId(results.getInt(1));
        item.setCustomOrder(results.getInt(2));
        item.setName(results.getString(3));
        item.setAlias(results.getString(4));
        item.setPartylist(results.getBoolean(5));
        return item;
    }
}
