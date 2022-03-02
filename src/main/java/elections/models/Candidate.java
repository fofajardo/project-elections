package elections.models;

public class Candidate {
    private int id;
    private int positionId;
    private int locationId;
    private int partyId;
    private String firstName;
    private String middleName;
    private String lastName;

    private Party attachedParty;
    
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

    public int getLocationId() {
        return locationId;
    }
    public void setLocationId(int locationId) {
        this.locationId = locationId;
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

    public Party getAttachedParty() {
        return attachedParty;
    }
    public void setAttachedParty(Party attachedParty) {
        this.attachedParty = attachedParty;
    }
}
