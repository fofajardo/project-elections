/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import elections.data.AccountDao;
import elections.data.CandidateDao;
import elections.data.PartyDao;
import elections.data.PositionDao;
import elections.data.ResponseDao;
import elections.models.Account;
import elections.models.Candidate;
import elections.models.Party;
import elections.models.Position;
import elections.models.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This servlet processes all requests for /ballot.
 */
@WebServlet("/ballot/*")
public class BallotController extends HttpServlet {
    private static final long serialVersionUID = 7992358694092514370L;

    /**
     * Path to the ballot view.
     */
    private static final String VIEW_BALLOT_COMMON = "/views/ballotView.jsp";
    /**
     * Path to the ballot form view.
     */
    private static final String VIEW_BALLOT_FORM = "/views/ballotForm.jsp";
    /**
     * Path to the empty ballot receipt view.
     */
    private static final String VIEW_BALLOT_RECEIPT_EMPTY = "/views/ballotReceiptEmpty.jsp";

    /**
     * Parameter prefix for each position in the ballot form.
     */
    private static final String parameterPrefix = "vote-position-";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Get path information and remove leading forward slash
        String pathInfo = StringUtils.stripEnd(request.getPathInfo(), "/").toLowerCase();
        String url = null;

        // Determine how to handle the current path
        if (pathInfo.equals("/candidates")) {
            // All: candidates listing
            url = goCandidates(request, response);
        } else if (ControllerUtils.redirectRole(request, response, 0)) {
            // Redirect to home page if not signed in
            return;
        } else {
            switch (pathInfo) {
            case "/answer":
                // SI: ballot form
                url = goAnswer(request, response);
                break;
            case "/status":
                // SI: ballot status
                url = goStatus(request, response);
                break;
            case "/receipt":
                // SI: ballot receipt
                url = goReceipt(request, response);
                break;
            default:
                break;
            }
        }

        ControllerUtils.respond(getServletContext(), request, response, url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Redirect to home page if not signed in
        if (ControllerUtils.redirectRole(request, response, 0)) {
            return;
        }

        // Get path information and remove leading forward slash
        String pathInfo = StringUtils.stripEnd(request.getPathInfo(), "/").toLowerCase();
        String url = null;

        if (pathInfo.equals("/submit")) {
            // SI: ballot submission
            url = goSubmit(request, response);
        }

        ControllerUtils.respond(getServletContext(), request, response, url);
    }

    /**
     * This method handles request to the ballot form page.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @return a path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    private String goAnswer(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Ballot");
        // Get the current signed in account
        Account account = ControllerUtils.getCurrentAccount(request);

        // Redirect back to ballot status page if the ballot
        // was already submitted
        if (isBallotSubmitted(account)) {
            return ControllerUtils.redirectStatus(request, response);
        }

        try {
            // Get a list of all positions
            List<Position> positions = PositionDao.findAll();
            request.setAttribute("positions",  positions);

            // Create a HashMap for storing the list of candidates
            // as the value, with their corresponding position as the key
            HashMap<Integer, List<Candidate>> candidates = new HashMap<>();
            // This array is used for determining the maximum number of
            // rows of candidate boxes for each position.
            int[] maxRows = new int[positions.size()];
            for (int i = 0; i < positions.size(); i++) {
                int positionId = positions.get(i).getId();
                // Get the list of candidates for this position
                List<Candidate> data = CandidateDao.findByPosition(positionId);
                candidates.put(positionId, data);
                // Compute for the maximum number of rows for this position
                // based on the number of candidates. This uses more or less
                // the same formula that COMELEC uses for their ballot faces
                maxRows[i] = data.size() / 4
                        + ((data.size() % 4 == 0) ? 0 : 1);
            }
            // Store these values as attributes
            request.setAttribute("candidates", candidates);
            request.setAttribute("maxRows", maxRows);

            // Get the list of party lists
            List<Party> partylists = PartyDao.findByPartylist();
            // Compute for the maximum number of rows for party lists
            // based the number of party lists
            int partylistMaxRows = partylists.size() / 4
                    + ((partylists.size() % 4 == 0) ? 0 : 1);
            // Store these values as attributes
            request.setAttribute("partylists", partylists);
            request.setAttribute("partylistMaxRows", partylistMaxRows);
        } catch (Exception e) {
            return null;
        }

        return VIEW_BALLOT_FORM;
    }

    /**
     * This method handles the POST request sent for submitting the ballot.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @return a path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    private String goSubmit(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        Account account = ControllerUtils.getCurrentAccount(request);

        // Redirect back to the ballot status page if the ballot
        // was already submitted
        if (isBallotSubmitted(account)) {
            ControllerUtils.redirectStatus(request, response);
        }

        // Get a list of all positions
        List<Position> positions = null;
        try {
            positions = PositionDao.findAll();
        } catch (SQLException e) {
            positions = new ArrayList<Position>();
        }

        Map<String, String[]> parameters = request.getParameterMap();
        Iterator<String> iterator = parameters.keySet().iterator();
        boolean failedValidation = false;

        while (iterator.hasNext()) {
            String key = iterator.next();
            String[] keyParts = key.split("-");

            // Ignore parameters that do not match the prefix or
            // is not equal to the expected number of parts
            if (!key.startsWith(parameterPrefix) && keyParts.length == 3) {
                continue;
            }

            // The vote limit for this position (used in validation)
            int voteLimit = 1;

            // Try to get the vote limit for this position if the current
            // parameter key is not for party lists
            boolean isCandidatePosition = !(keyParts[2].equals("partylist"));
            if (isCandidatePosition) {
                // Attempt to convert the string position
                // identifier to an integer
                int positionId = 0;
                try {
                    positionId = Integer.parseInt(keyParts[2]);
                } catch (Exception e) {
                    continue;
                }
                // Get the vote limit from the database
                Position position = positions.get(positionId - 1);
                voteLimit = position.getVoteLimit();
            }

            // Get the array containing values for the chosen candidates/party lists
            String[] values = parameters.get(key);

            // Fail the validation and break the loop if it
            // is not equal to the vote limit
            if (values.length != voteLimit) {
                failedValidation = true;
                break;
            }

            // Add votes to database
            for (int i = 0; i < values.length; i++) {
                // Create a new response object
                try {
                    Response vote = new Response();
                    // Try to parse the candidate/party list identifier
                    int targetId = 0;
                    targetId = Integer.parseInt(values[i]);

                    // Set the account identifier of the response object
                    // using information from the currently signed in account
                    vote.setVoterId(account.getId());
                    if (isCandidatePosition) {
                        vote.setCandidateId(targetId);
                    } else {
                        vote.setPartylistId(targetId);
                    }

                    // Create the response record
                    ResponseDao.create(vote);
                } catch (Exception e) {
                    continue;
                }
            }
        }

        // Return to the answer ballot page if validation failed
        if (failedValidation) {
            return goAnswer(request, response);
        }

        // Set the timestamp of the vote recorded and update
        // the account data
        account.setVoteRecorded(Date.from(Instant.now()));
        try {
            AccountDao.update(account);
        } catch (SQLException e) {
            // Ignore exception from failed account update
        }

        // Redirect to the ballot status page
        return ControllerUtils.redirectStatus(request, response);
    }

    /**
     * This method handles the ballot status page.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @return a path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    private String goStatus(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Ballot");
        // Get the current signed in account
        Account account = ControllerUtils.getCurrentAccount(request);
        // Set the attribute if the ballot was already submitted
        if (isBallotSubmitted(account)) {
            request.setAttribute("ballotSubmitted", true);
        }
        // Return the target view path
        return "/views/ballotStatus.jsp";
    }

    /**
     * This method handles the ballot receipt page.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @return a path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    private String goReceipt(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Receipt");
        // Get the current signed in account
        request.setAttribute("isReceipt", true);
        Account account = ControllerUtils.getCurrentAccount(request);
        // Populate the details if the ballot was already submitted
        if (isBallotSubmitted(account)) {
            try {
                // Get a list of all positions
                List<Position> positions = PositionDao.findAll();
                request.setAttribute("positions",  positions);

                // Create a HashMap for storing the list of all candidates
                // per position and create empty array lists.
                HashMap<Integer, ArrayList<Candidate>> candidates = new HashMap<>();
                for (int i = 0; i < positions.size(); i++) {
                    Position position = positions.get(i);
                    candidates.put(position.getId(), new ArrayList<Candidate>());
                }

                // Get the list of all the voted candidates by this account
                List<Response> candidateVotes = ResponseDao.findByAccountAndCandidate(account.getId());
                for (int i = 0; i < candidateVotes.size(); i++) {
                    Response vote = candidateVotes.get(i);
                    Candidate candidate = vote.getAttachedCandidate();
                    candidates.get(candidate.getPositionId()).add(candidate);
                }

                // Compute for the maximum number of rows for candidates
                // based the number of candidates
                int[] maxRows = new int[positions.size()];
                for (int i = 0; i < positions.size(); i++) {
                    Position position = positions.get(i);
                    ArrayList<Candidate> data = candidates.get(position.getId());
                    maxRows[i] = data.size() / 4 + ((data.size() % 4 == 0) ? 0 : 1);
                }

                // Store these as attributes
                request.setAttribute("candidates", candidates);
                request.setAttribute("maxRows", maxRows);

                // Get a list of all the voted party lists by this account
                List<Response> partylistVotes = ResponseDao.findByAccountAndPartylist(account.getId());
                List<Party> partylists = new ArrayList<>();
                for (int i = 0; i < partylistVotes.size(); i++) {
                    Response vote = partylistVotes.get(i);
                    partylists.add(vote.getAttachedParty());
                }
                // Compute for the maximum number of rows for party lists
                // based the number of party lists
                int partylistMaxRows = partylists.size() / 4 + ((partylists.size() % 4 == 0) ? 0 : 1);

                // Store these as attributes
                request.setAttribute("partylists", partylists);
                request.setAttribute("partylistMaxRows", partylistMaxRows);
            } catch (SQLException e) {
                return null;
            }

            // Set the retrieval attribute to the date/time when the
            // vote was recorded (used in the badge)
            request.setAttribute("retrieval", account.getVoteRecorded());

            // Return the target view path
            return VIEW_BALLOT_COMMON;
        }
        // Otherwise, return the empty ballot receipt view path
        return VIEW_BALLOT_RECEIPT_EMPTY;
    }

    /**
     * This method handles the candidates listing page.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @return a path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    private String goCandidates(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Candidates");

        try {
            // Get a list of all positions
            List<Position> positions = PositionDao.findAll();
            request.setAttribute("positions",  positions);

            // Create a HashMap for storing the list of all candidates
            // and store it as an attribute
            HashMap<Integer, List<Candidate>> candidates = new HashMap<>();
            int[] maxRows = new int[positions.size()];
            for (int i = 0; i < positions.size(); i++) {
                int positionId = positions.get(i).getId();
                List<Candidate> data = CandidateDao.findByPosition(positionId);
                candidates.put(positionId, data);
                // Compute for the maximum number of rows for candidates
                // based the number of candidates
                maxRows[i] = data.size() / 4
                        + ((data.size() % 4 == 0) ? 0 : 1);
            }
            request.setAttribute("candidates", candidates);
            request.setAttribute("maxRows", maxRows);

            // Get the list of all partylists with their votes and store
            // it as an attribute
            List<Party> partylists = PartyDao.findByPartylist();
            // Compute for the maximum number of rows for party lists
            // based the number of party lists
            int partylistMaxRows = partylists.size() / 4
                    + ((partylists.size() % 4 == 0) ? 0 : 1);
            request.setAttribute("partylists", partylists);
            request.setAttribute("partylistMaxRows", partylistMaxRows);
        } catch (SQLException e) {
            return null;
        }

        return VIEW_BALLOT_COMMON;
    }

    /**
     * Returns whether the ballot for this account was already submitted.
     * @param account the {@link Account} object
     * @return a {@code boolean} determining whether the ballot for this
     *         account was already submitted.
     */
    private boolean isBallotSubmitted(Account account) {
        if (account != null) {
            Date ballotSubmissionDate = account.getVoteRecorded();
            return (ballotSubmissionDate != null);
        }
        return false;
    }
}
