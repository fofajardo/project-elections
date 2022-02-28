package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class PositionDB {
    public static void create(Position position) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "INSERT INTO `positions` "
                         + "(`position_name`, `position_alias`, `vote_limit`)"
                         + "VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);

            statement.setString(1, position.getName());
            statement.setString(2, position.getAlias());
            statement.setInt(3, position.getVoteLimit());

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static ArrayList<Position> read() throws SQLException {
        ArrayList<Position> itemList = new ArrayList<Position>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `positions`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Position item = new Position();
                item.setId(results.getInt(1));
                item.setName(results.getString(2));
                item.setAlias(results.getString(3));
                item.setVoteLimit(results.getInt(4));
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static void update(Position position) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "UPDATE `positions` SET"
                         + "    `position_name`=?, "
                         + "    `position_alias`=?, "
                         + "    `vote_limit`=? "
                         + "WHERE `id`=?";
            statement = connection.prepareStatement(query);
    
            statement.setString(1, position.getName());
            statement.setString(2, position.getAlias());
            statement.setInt(3, position.getVoteLimit());
            statement.setInt(4, position.getId());

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

            String query = "DELETE FROM `positions` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(Position position) throws SQLException {
        delete(position.getId());
    }
}
