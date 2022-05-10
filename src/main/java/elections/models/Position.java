/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections.models;

public class Position {
    private int id;
    private String name;
    private String alias;
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

    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getVoteLimit() {
        return voteLimit;
    }
    public void setVoteLimit(int voteLimit) {
        this.voteLimit = voteLimit;
    }
}
