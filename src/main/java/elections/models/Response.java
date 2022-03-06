package elections.models;

public class Response {
    private int voterId;
    private int candidateId;
    private int partylistId;
    
    private Candidate attachedCandidate;
    private Party attachedParty;

    public int getVoterId() {
        return voterId;
    }
    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }

    public int getCandidateId() {
        return candidateId;
    }
    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getPartylistId() {
        return partylistId;
    }
    public void setPartylistId(int partylistId) {
        this.partylistId = partylistId;
    }

    public Candidate getAttachedCandidate() {
        return attachedCandidate;
    }
    public void setAttachedCandidate(Candidate attachedCandidate) {
        this.attachedCandidate = attachedCandidate;
    }

    public Party getAttachedParty() {
        return attachedParty;
    }
    public void setAttachedParty(Party attachedParty) {
        this.attachedParty = attachedParty;
    }
}
