package elections.models;

public class Response {
    private int voterId;
    private int candidateId;
    private int partylistId;

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
}
