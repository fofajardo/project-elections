/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.cliftonlabs.json_simple.JsonObject;

import elections.data.CandidateDao;
import elections.data.PartyDao;
import elections.data.PositionDao;
import elections.models.Candidate;
import elections.models.Party;
import elections.models.Position;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This servlet processes all requests for /results.
 */
@WebServlet("/results/*")
public class ResultsController extends HttpServlet {
    private static final long serialVersionUID = -142187939623509019L;

    /**
     * Path to the ballot view.
     */
    private static final String VIEW_BALLOT_COMMON = "/views/ballotView.jsp";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Get path information and remove leading forward slash
        String pathInfo = StringUtils.stripEnd(request.getPathInfo(), "/").toLowerCase();
        String url = null;

        // Determine how to handle the current path
        switch (pathInfo) {
        case "/view":
            // All: View election results
            url = goView(request, response);
            break;
        case "/get":
            // All: Get JSON copy of leading candidates
            url = goSendJson(request, response);
            break;
        default:
            break;
        }

        ControllerUtils.respond(getServletContext(), request, response, url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * This method handles the election results page.
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
    private String goView(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Results");

        try {
            // Get all positions available for the candidates
            // and store it as an attribute
            List<Position> positions = PositionDao.findAll();
            request.setAttribute("positions",  positions);

            // Create a HashMap for storing the list of all candidates
            // with their votes per position and store it as an attribute
            HashMap<Integer, List<Candidate>> candidates = new HashMap<>();
            for (int i = 0; i < positions.size(); i++) {
                int positionId = positions.get(i).getId();
                List<Candidate> data = CandidateDao.findByPositionWithVotes(positionId, 0);
                candidates.put(positionId, data);
            }
            request.setAttribute("candidates", candidates);

            // Get the list of all partylists with their votes and store
            // it as an attribute
            List<Party> partylists = PartyDao.findByPartylistWithVotes(0);
            request.setAttribute("partylists", partylists);

            // Set the retrieval date which will be used in the
            // "As of" badge of the election results view
            request.setAttribute("retrieval", new Date());
        } catch (Exception e) {
            return null;
        }

        // Tell the view that this is an election results page
        request.setAttribute("isResults", true);
        return VIEW_BALLOT_COMMON;
    }

    /**
     * This method handles the request to get a JSON copy of the
     * leading candidates (top 12) in the election results.
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @apiNote This is used to provide data for generating charts
     *          in the client side with Chart.js
     */
    private String goSendJson(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Set the correct content type of the response (JSON/UTF-8)
        response.setContentType("application/json; charset=UTF-8");
        JsonObject results = new JsonObject();
        try {
            // Get the list of all positions and loop through it
            List<Position> positions = PositionDao.findAll();
            for (int i = 0; i < positions.size(); i++) {
                int positionId = positions.get(i).getId();
                // Get the list of the 12 leading candidates with
                // their votes for the current position
                List<Candidate> data = CandidateDao.findByPositionWithVotes(
                        positionId, 12);
                // Create lists for the labels and raw data
                List<String> graphLabels = new ArrayList<>();
                List<Integer> graphData = new ArrayList<>();
                // Loop through the list of candidates, format and
                // add them to the lists for labels and raw data
                for (int j = 0; j < data.size(); j++) {
                    Candidate candidate = data.get(j);
                    // Format the full name of the candidate
                    String fullName = String.format(
                            "%s, %s %s %s",
                            candidate.getLastName(),
                            candidate.getFirstName(),
                            candidate.getMiddleName(),
                            candidate.getSuffix()).trim();
                    graphLabels.add(fullName);
                    graphData.add(candidate.getVotes());
                }
                // Create a JsonObject and insert all the required data
                JsonObject positionJson = new JsonObject();
                positionJson.put("labels", graphLabels);
                positionJson.put("data", graphData);
                positionJson.put("id", positionId);
                // Insert the position object as a property
                // to the resulting JSON
                results.put(String.valueOf(i), positionJson);
            }
        } catch (Exception e) {
            // Insert the error message to the resulting JSON
            // if an exception was encountered
            results.put("error", e.getMessage());
        }
        // Print out the formatted JSON string from the results JsonObject
        PrintWriter writer = response.getWriter();
        writer.print(results.toJson());
        // No view is associated with this request
        return "";
    }
}
