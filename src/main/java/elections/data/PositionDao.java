/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import elections.models.Position;

/**
 * This class defines static methods for accessing data related to positions.
 */
public class PositionDao {
    /**
     * Inserts a record using data from the specified position.
     *
     * @param position the {@link Position} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void create(Position position) throws SQLException {
        String sql = "INSERT INTO `positions` ("
                + "    `position_name`, `position_alias`, `vote_limit`"
                + ") VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
            statement.setString(1, position.getName());
            statement.setString(2, position.getAlias());
            statement.setInt(3, position.getVoteLimit());
            statement.executeUpdate();
        }
    }

    /**
     * Returns a list containing all position records.
     *
     * @return a list containing {@link Position} objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Position> findAll() throws SQLException {
        List<Position> itemList = new ArrayList<Position>();
        String sql = "SELECT * FROM `positions`";
        try (Connection connection = ConnectionUtils.getConnection();
                Statement statement = connection.createStatement()) {
            // Iterate over the results, create a Position object
            // based on the data, and add them to the list
            try (ResultSet results = statement.executeQuery(sql)) {
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
     * Updates a record using data from the specified position.
     *
     * @param position the {@link Position} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void update(Position position) throws SQLException {
        String sql = "UPDATE `positions` SET"
                + "    `position_name`=?, "
                + "    `position_alias`=?, "
                + "    `vote_limit`=? "
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters that correspond to the statement
            statement.setString(1, position.getName());
            statement.setString(2, position.getAlias());
            statement.setInt(3, position.getVoteLimit());
            statement.setInt(4, position.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Deletes a record matching the specified position identifier.
     *
     * @param positionId the position identifier
     * @throws SQLException if a database access error occurs
     */
    public static void delete(int positionId) throws SQLException {
        String sql = "DELETE FROM `positions` WHERE `id`=?";
        try (Connection connection = ConnectionUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, positionId);
            statement.executeUpdate(sql);
        }
    }

    /**
     * Deletes a record matching the identifier of the specified position.
     *
     * @param position the {@link Position} object whose data will be used
     * @throws SQLException if a database access error occurs
     */
    public static void delete(Position position) throws SQLException {
        delete(position.getId());
    }
}
