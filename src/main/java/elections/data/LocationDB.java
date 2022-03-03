package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class LocationDB {
    public static void create(Location location) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "INSERT INTO `locations` "
                         + "(`location_name`)"
                         + "VALUES (?)";
            statement = connection.prepareStatement(query);
    
            statement.setString(1, location.getName());

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static ArrayList<Location> read() throws SQLException {
        ArrayList<Location> itemList = new ArrayList<Location>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `locations`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Location item = new Location();
                item.setId(results.getInt(1));
                item.setName(results.getString(2));
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static Location readId(int id) throws SQLException {
        Location item = null;
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `locations` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                item = new Location();
                item.setId(results.getInt(1));
                item.setName(results.getString(2));
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return item;
    }
    
    public static void update(Location location) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "UPDATE `locations` SET"
                         + "    `location_name`=? "
                         + "    WHERE `id`=?";
            statement = connection.prepareStatement(query);
    
            statement.setString(1, location.getName());
            statement.setInt(2, location.getId());

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

            String query = "DELETE FROM `locations` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(Location location) throws SQLException {
        delete(location.getId());
    }
}
