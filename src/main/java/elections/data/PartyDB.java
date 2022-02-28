package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class PartyDB {
    public static void create(Party party) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "INSERT INTO `parties` "
                         + "(`custom_order`, `party_name`, `party_alias`, `is_partylist`)"
                         + "VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query);

            statement.setInt(1, party.getCustomOrder());
            statement.setString(2, party.getName());
            statement.setString(3, party.getAlias());
            statement.setBoolean(4, party.isPartylist());

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static ArrayList<Party> read() throws SQLException {
        ArrayList<Party> itemList = new ArrayList<Party>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `parties`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Party item = new Party();
                item.setId(results.getInt(1));
                item.setCustomOrder(results.getInt(2));
                item.setName(results.getString(3));
                item.setAlias(results.getString(4));
                item.setPartylist(results.getBoolean(5));
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static void update(Party party) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "UPDATE `parties` SET"
                         + "    `custom_order`=?, "
                         + "    `party_name`=?, "
                         + "    `party_alias`=?, "
                         + "    `is_partylist`=? "
                         + "WHERE `id`=?";
            statement = connection.prepareStatement(query);
    
            statement.setInt(1, party.getCustomOrder());
            statement.setString(2, party.getName());
            statement.setString(3, party.getAlias());
            statement.setBoolean(4, party.isPartylist());
            statement.setInt(5, party.getId());

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

            String query = "DELETE FROM `parties` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(Party party) throws SQLException {
        delete(party.getId());
    }
}
