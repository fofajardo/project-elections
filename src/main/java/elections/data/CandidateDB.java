package elections.data;

import java.sql.*;
import java.util.*;
import elections.models.*;

public class CandidateDB {
    public static void create(Candidate candidate) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "INSERT INTO `candidates` ("
                         + "`position_id`, `partylist_id`, `location_id`, "
                         + "`first_name`, `middle_name`, `last_name`"
                         + ") VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
    
            statement.setInt(1, candidate.getPositionId());
            statement.setInt(2, candidate.getPartyId());
            statement.setInt(3, candidate.getLocationId());
            statement.setString(4, candidate.getFirstName());
            statement.setString(5, candidate.getMiddleName());
            statement.setString(6, candidate.getLastName());

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static ArrayList<Candidate> read() throws SQLException {
        ArrayList<Candidate> itemList = new ArrayList<Candidate>();
        Statement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `candidates`";
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Candidate item = new Candidate();
                item.setId(results.getInt(1));
                item.setPositionId(results.getInt(2));
                item.setPartyId(results.getInt(3));
                item.setLocationId(results.getInt(4));
                item.setFirstName(results.getString(5));
                item.setMiddleName(results.getString(6));
                item.setLastName(results.getString(7));
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static ArrayList<Candidate> readFromPosition(int positionId, boolean attachParty) throws SQLException {
        ArrayList<Candidate> itemList = new ArrayList<Candidate>();
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            String query = "SELECT * FROM `candidates` WHERE `position_id`=?";
            if (attachParty) {
                query = "SELECT * FROM `candidates` LEFT JOIN `parties` "
                      + "ON `candidates`.partylist_id = `parties`.id "
                      + "WHERE `position_id`=? ORDER BY `last_name`";
            }
            statement = connection.prepareStatement(query);
            statement.setInt(1, positionId);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Candidate item = new Candidate();
                item.setId(results.getInt(1));
                item.setPositionId(results.getInt(2));
                item.setPartyId(results.getInt(3));
                item.setLocationId(results.getInt(4));
                item.setFirstName(results.getString(5));
                item.setMiddleName(results.getString(6));
                item.setLastName(results.getString(7));
                if (attachParty) {
                    int partyId = results.getInt(8);
                    if (partyId > 0) {
                        Party attachedParty = new Party();
                        attachedParty.setId(partyId);
                        attachedParty.setCustomOrder(results.getInt(9));
                        attachedParty.setName(results.getString(10));
                        attachedParty.setAlias(results.getString(11));
                        attachedParty.setPartylist(results.getBoolean(12));
                        item.setAttachedParty(attachedParty);
                    }
                }
                itemList.add(item);
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return itemList;
    }

    public static void update(Candidate candidate) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = ConnectionUtil.getConnection();
    
            String query = "UPDATE `candidates` SET"
                         + "    `position_id`=?, "
                         + "    `partylist_id`=?, "
                         + "    `location_id`=?,"
                         + "    `first_name`=?,"
                         + "    `middle_name`=?,"
                         + "    `last_name`=?"
                         + "WHERE `id`=?";
            statement = connection.prepareStatement(query);
    
            statement.setInt(1, candidate.getPositionId());
            statement.setInt(2, candidate.getPartyId());
            statement.setInt(3, candidate.getLocationId());
            statement.setString(4, candidate.getFirstName());
            statement.setString(5, candidate.getMiddleName());
            statement.setString(6, candidate.getLastName());
            statement.setInt(7, candidate.getId());

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

            String query = "DELETE FROM `candidates` WHERE `id`=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static void delete(Candidate candidate) throws SQLException {
        delete(candidate.getId());
    }
}
