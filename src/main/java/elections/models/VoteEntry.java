package elections.models;

public class VoteEntry {
    private int voterId;
    private int candidateId;

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
}
