package elections.models;

public class Position {
    private int id;
    private String name;
    private int voteLimit;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getVoteLimit() {
        return voteLimit;
    }
    public void setVoteLimit(int voteLimit) {
        this.voteLimit = voteLimit;
    }
}
