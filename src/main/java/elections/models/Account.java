package elections.models;

import java.util.Date;
import java.util.UUID;

public class Account {
    private int id;
    private String uuid;
    
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;

    private String username;
    private String email;
    private String password;

    private Date lastSignIn;
    private Date voteRecorded;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public void randomizeUuid() {
        this.uuid = UUID.randomUUID().toString();
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
