package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class PartyDao {
    /**
     * Inserts a new party record.
     * @param party a {@code Party} object from which to read the data 
     * @throws SQLException if a database access error occurs
     */
    public static void create(Party party) throws SQLException {
        String query = "INSERT INTO `parties` "
                + "(`custom_order`, `party_name`, `party_alias`, `is_partylist`)"
                + "VALUES (?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters that correspond to the query
            statement.setInt(1, party.getCustomOrder());
            statement.setString(2, party.getName());
            statement.setString(3, party.getAlias());
            statement.setBoolean(4, party.isPartylist());
            statement.executeUpdate();
        }
    }

    /***
     * Retrieves all party records.
     * @return a {@code List<Party>} collection containing {@code Party} objects
     * @throws SQLException
     */
    public static List<Party> findAll() throws SQLException {
        List<Party> itemList = new ArrayList<Party>();
        String query = "SELECT * FROM `parties`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Party object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Party item = createFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Retrieves a party record matching the given ID.
     * @param id the party ID
     * @return a {@code Party} object of the matching record
     * @throws SQLException if a database access error occurs
     */
    public static Party findById(int partyId) throws SQLException {
        Party item = null;
        String query = "SELECT * FROM `parties` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, partyId);
            // Get the first result and create a Party object
            // based on the data
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    item = createFromResultSet(results);
                }
            }
        }
        return item;
    }

    /**
     * Retrieves party list records.
     * @return a {@code List<Party>} collection containing {@code Party} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Party> findByPartylist() throws SQLException {
        List<Party> itemList = new ArrayList<Party>();
        String query = "SELECT * FROM `parties` WHERE `is_partylist`=1 ORDER BY `custom_order`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Party object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Party item = createFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Retrieves party list records, including the number of
     * votes cast for each party list.
     * @param limit the limit number of results, use {@code 0}
     *        if there's no limit
     * @return a {@code List<Party>} collection containing {@code Party} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Party> findByPartylistWithVotes(int limit)
           throws SQLException {
        List<Party> itemList = new ArrayList<Party>();
        String query = "SELECT * FROM `parties`"
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
            query += " LIMIT " + limit;
        }
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Party object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Party item = createFromResultSet(results);
                    // Get number of votes from joined votes column
                    item.setVotes(results.getInt("votes"));
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Modifies an existing party record.
     * @param party an {@code Party} object from which to read the data 
     * @throws SQLException if a database access error occurs
     */
    public static void update(Party party) throws SQLException {
        String query = "UPDATE `parties` SET"
                + "    `custom_order`=?, "
                + "    `party_name`=?, "
                + "    `party_alias`=?, "
                + "    `is_partylist`=? "
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters that correspond to the query
            statement.setInt(1, party.getCustomOrder());
            statement.setString(2, party.getName());
            statement.setString(3, party.getAlias());
            statement.setBoolean(4, party.isPartylist());
            statement.setInt(5, party.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Deletes a party record matching the given ID. 
     * @param id the party ID
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM `parties` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }

    /**
     * Deletes a party record matching the ID of the given object. 
     * @param party the {@code Party} object
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Party party) throws SQLException {
        delete(party.getId());
    }

    /**
     * Creates a {@code Party} object using the given {@code ResultSet}.
     * @param results the {@code ResultSet} from which values will be retrieved
     * @return a {@code Party} object
     * @throws SQLException if a database access error occurs
     */
    private static Party createFromResultSet(ResultSet results)
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
