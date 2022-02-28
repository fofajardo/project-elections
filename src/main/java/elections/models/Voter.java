package elections.models;

import java.time.LocalDateTime;

public class Voter {
    private int id;
    private int locationId;
    
    private String firstName;
    private String middleName;
    private String lastName;
    
    private String voterUuid;
    private String voterHash;
    
    private LocalDateTime lastSignIn;
    private LocalDateTime voteRecorded; 

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

    public String getVoterHash() {
        return voterHash;
    }
    public void setVoterHash(String voterHash) {
        this.voterHash = voterHash;
    }

    public LocalDateTime getLastSignIn() {
        return lastSignIn;
    }
    public void setLastSignIn(LocalDateTime lastSignIn) {
        this.lastSignIn = lastSignIn;
    }

    public LocalDateTime getVoteRecorded() {
        return voteRecorded;
    }
    public void setVoteRecorded(LocalDateTime voteRecorded) {
        this.voteRecorded = voteRecorded;
    }
}
