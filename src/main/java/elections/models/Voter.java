package elections.models;

import java.util.Date;
import java.util.UUID;

public class Voter {
    private int id;
    private int locationId;
    
    private String firstName;
    private String middleName;
    private String lastName;
    
    private String voterUuid;
    private String voterHash;
    
    private Date lastSignIn;
    private Date voteRecorded;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getLocationId() {
        return locationId;
    }
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getVoterUuid() {
        return voterUuid;
    }
    public void setVoterUuid(String voterUuid) {
        this.voterUuid = voterUuid;
    }

    public void randomizeVoterUuid() {
        this.voterUuid = UUID.randomUUID().toString();
    }
    
    public String getVoterHash() {
        return voterHash;
    }
    public void setVoterHash(String voterHash) {
        this.voterHash = voterHash;
    }

    public Date getLastSignIn() {
        return lastSignIn;
    }
    public void setLastSignIn(Date lastSignIn) {
        this.lastSignIn = lastSignIn;
    }

    public Date getVoteRecorded() {
        return voteRecorded;
    }
    public void setVoteRecorded(Date voteRecorded) {
        this.voteRecorded = voteRecorded;
    }
}
