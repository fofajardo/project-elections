package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class PositionDao {
    /**
     * Inserts a new position record.
     * @param position a {@code Position} object from which to read the data
     * @throws SQLException if a database access error occurs
     */
    public static void create(Position position) throws SQLException {
        String query = "INSERT INTO `positions` "
                + "(`position_name`, `position_alias`, `vote_limit`)"
                + "VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters that correspond to the query
            statement.setString(1, position.getName());
            statement.setString(2, position.getAlias());
            statement.setInt(3, position.getVoteLimit());
            statement.executeUpdate();
        }
    }

    /**
     * Retrieves all position records.
     * @return a {@code List<Position>} collection containing {@code Position} objects 
     * @throws SQLException
     */
    public static List<Position> findAll() throws SQLException {
        List<Position> itemList = new ArrayList<Position>();
        String query = "SELECT * FROM `positions`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Position object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    Position item = new Position();
                    item.setId(results.getInt(1));
                    item.setName(results.getString(2));
                    item.setAlias(results.getString(3));
                    item.setVoteLimit(results.getInt(4));
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    /**
     * Modifies an existing position record.
     * @param position an {@code Position} object from which to read the data 
     * @throws SQLException if a database access error occurs
     */
    public static void update(Position position) throws SQLException {
        String query = "UPDATE `positions` SET"
                + "    `position_name`=?, "
                + "    `position_alias`=?, "
                + "    `vote_limit`=? "
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters that correspond to the query
            statement.setString(1, position.getName());
            statement.setString(2, position.getAlias());
            statement.setInt(3, position.getVoteLimit());
            statement.setInt(4, position.getId());
            statement.executeUpdate();
        }        
    }

    /**
     * Deletes a position record matching the given ID.
     * @param id the position ID
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM `positions` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }

    /**
     * Deletes a position record matching the ID of the given object.
     * @param position the {@code Position} object
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Position position) throws SQLException {
        delete(position.getId());
    }
}
