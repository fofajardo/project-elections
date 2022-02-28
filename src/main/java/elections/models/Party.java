package elections.models;

public class Party {
    private int id;
    private int customOrder;
    private String name;
    private String alias;
    private boolean isPartylist;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCustomOrder() {
        return customOrder;
    }
    public void setCustomOrder(int customOrder) {
        this.customOrder = customOrder;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isPartylist() {
        return isPartylist;
    }
    public void setPartylist(boolean isPartylist) {
        this.isPartylist = isPartylist;
    }
}
