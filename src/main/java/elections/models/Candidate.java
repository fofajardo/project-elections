package elections.models;

public class Candidate {
    private int id;
    private int positionId;
    private int partyId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;

    private Party attachedParty;
    private int votes;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getPositionId() {
        return positionId;
    }
    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getPartyId() {
        return partyId;
    }
    public void setPartyId(int partyId) {
        this.partyId = partyId;
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

    public String getSuffix() {
        return suffix;
    }
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Party getAttachedParty() {
        return attachedParty;
    }
    public void setAttachedParty(Party attachedParty) {
        this.attachedParty = attachedParty;
    }

    public int getVotes() {
        return votes;
    }
    public void setVotes(int votes) {
        this.votes = votes;
    }
}
