/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

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
