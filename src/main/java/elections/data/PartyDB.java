package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class PartyDB {
    private static Party createFromResultSet(ResultSet results)
            throws SQLException {
        Party item = new Party();
        item.setId(results.getInt(1));
        item.setCustomOrder(results.getInt(2));
        item.setName(results.getString(3));
        item.setAlias(results.getString(4));
        item.setPartylist(results.getBoolean(5));
        return item;
    }

    public static void create(Party party) throws SQLException {
        String query = "INSERT INTO `parties` "
                + "(`custom_order`, `party_name`, `party_alias`, `is_partylist`)"
                + "VALUES (?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, party.getCustomOrder());
            statement.setString(2, party.getName());
            statement.setString(3, party.getAlias());
            statement.setBoolean(4, party.isPartylist());

            statement.executeUpdate();
        }
    }

    public static ArrayList<Party> read() throws SQLException {
        ArrayList<Party> itemList = new ArrayList<Party>();
        String query = "SELECT * FROM `parties`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Party item = createFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public static Party readId(int partyId) throws SQLException {
        Party item = null;
        String query = "SELECT * FROM `parties` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, partyId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    item = createFromResultSet(results);
                }
            }
        }
        return item;
    }

    public static ArrayList<Party> readPartylist() throws SQLException {
        ArrayList<Party> itemList = new ArrayList<Party>();
        String query = "SELECT * FROM `parties` WHERE `is_partylist`=1 ORDER BY `custom_order`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Party item = createFromResultSet(results);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public static ArrayList<Party> readPartylistWithVotes(int limit)
           throws SQLException {
        ArrayList<Party> itemList = new ArrayList<Party>();
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
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Party item = createFromResultSet(results);
                    item.setVotes(results.getInt("votes"));
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public static void update(Party party) throws SQLException {
        String query = "UPDATE `parties` SET"
                + "    `custom_order`=?, "
                + "    `party_name`=?, "
                + "    `party_alias`=?, "
                + "    `is_partylist`=? "
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, party.getCustomOrder());
            statement.setString(2, party.getName());
            statement.setString(3, party.getAlias());
            statement.setBoolean(4, party.isPartylist());
            statement.setInt(5, party.getId());

            statement.executeUpdate();
        }
    }

    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM `parties` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }

    public static void delete(Party party) throws SQLException {
        delete(party.getId());
    }
}
