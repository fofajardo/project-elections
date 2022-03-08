package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class PositionDB {
    public static void create(Position position) throws SQLException {
        String query = "INSERT INTO `positions` "
                + "(`position_name`, `position_alias`, `vote_limit`)"
                + "VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, position.getName());
            statement.setString(2, position.getAlias());
            statement.setInt(3, position.getVoteLimit());

            statement.executeUpdate();
        }
    }

    public static ArrayList<Position> read() throws SQLException {
        ArrayList<Position> itemList = new ArrayList<Position>();
        String query = "SELECT * FROM `positions`";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
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

    public static void update(Position position) throws SQLException {
        String query = "UPDATE `positions` SET"
                + "    `position_name`=?, "
                + "    `position_alias`=?, "
                + "    `vote_limit`=? "
                + "    WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, position.getName());
            statement.setString(2, position.getAlias());
            statement.setInt(3, position.getVoteLimit());
            statement.setInt(4, position.getId());

            statement.executeUpdate();
        }        
    }

    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM `positions` WHERE `id`=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }

    public static void delete(Position position) throws SQLException {
        delete(position.getId());
    }
}
