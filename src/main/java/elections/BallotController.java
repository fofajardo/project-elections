package elections;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import elections.data.*;
import elections.models.*;

@WebServlet("/ballot/*")
public class BallotController extends HttpServlet {
    private static final long serialVersionUID = 1;
    public static final String statusUrl = "/ballot/status";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    if (AccountController.redirectGuest(request, response)) {
	        return;
	    }

	    String requestURI = request.getRequestURI();
	    String url = null;

	    if (requestURI.endsWith("/answer")) {
	        url = goAnswer(request, response);
	    } else if (requestURI.endsWith("/status")) {
	        url = goStatus(request, response);
	    }

	    doRespond(request, response, url);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (AccountController.redirectGuest(request, response)) {
            return;
        }

        String requestURI = request.getRequestURI();
	    String url = null;

	    if (requestURI.endsWith("/submit")) {
            url = goSubmit(request, response);
        }
	    
	    doRespond(request, response, url);
	}

	private void doRespond(
            HttpServletRequest request,
            HttpServletResponse response,
            String url)
            throws ServletException, IOException {
        request.setAttribute("navActiveBallot", "active");

	    if (url == null) {
            response.sendError(404);
        } else if (!url.isBlank()) {
            getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
        }	    
	}
	
	private String goAnswer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Account account = AccountController.getCurrentAccount(request);

	    if (isBallotSubmitted(account)) {
	        response.sendRedirect(request.getContextPath() + statusUrl);
	        return "";
	    }

	    try {
            Location currentLocation = LocationDB.readId(account.getLocationId());
            request.setAttribute("currentLocation", currentLocation);

            ArrayList<Position> positions = PositionDB.read();
            request.setAttribute("positions",  positions);

            ArrayList<ArrayList<Candidate>> candidates = new ArrayList<ArrayList<Candidate>>();
            int[] maxRows = new int[positions.size()];
            for (int i = 0; i < positions.size(); i++) {
                Position item = positions.get(i);
                ArrayList<Candidate> data = CandidateDB.readFromPosition(item.getId(), true);
                candidates.add(data);
                maxRows[i] = data.size() / 4 + ((data.size() % 4 == 0) ? 0 : 1);
            }
            request.setAttribute("candidates", candidates);
            request.setAttribute("maxRows", maxRows);
            
            ArrayList<Party> partylists = PartyDB.readPartylist();
            int partylistMaxRows = partylists.size() / 4 + ((partylists.size() % 4 == 0) ? 0 : 1);
            request.setAttribute("partylists", partylists);
            request.setAttribute("partylistMaxRows", partylistMaxRows);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "/views/ballotForm.jsp";
	}

	private static final String parameterPrefix = "vote-position-";
	
	private String goSubmit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Account account = AccountController.getCurrentAccount(request);

	    if (!isBallotSubmitted(account)) {
            ArrayList<Position> positions = null;
            try {
                positions = PositionDB.read();
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
    
                int voteLimit = 1;
    
                boolean isPartylist = (keyParts[2].equals("partylist"));
    	        if (!isPartylist) {
    	            int positionId = 0;
                    try {
                        positionId = Integer.parseInt(keyParts[2]);                
                    } catch (Exception e) {
                        continue;
                    }
    
                    Position position = positions.get(positionId - 1);
                    voteLimit = position.getVoteLimit();
    	        }
    
    	        String[] values = parameters.get(key);
                if (values.length != voteLimit) {
                    failedValidation = true;
                    break;
                }
    
                // Add votes to database
    	        for (int i = 0; i < values.length; i++) {
                    try {
        	            Response vote = new Response();
        	            int targetId = 0;
        	                targetId = Integer.parseInt(values[i]);                
        	            
        	            vote.setVoterId(account.getId());
        	            if (isPartylist) {
        	                vote.setPartylistId(targetId);
        	            } else {
        	                vote.setCandidateId(targetId);
        	            }
    
                        ResponseDB.create(vote);
                    } catch (Exception e) {
                        continue;
                    }
    	        }
    	    }
    
    	    if (failedValidation) {
    	        return goAnswer(request, response);
    	    }
    
    	    account.setVoteRecorded(Date.from(Instant.now()));
    	    try {
                AccountDB.update(account);
            } catch (SQLException e) {
                // Ignore exception from failed account update
            }
        }

	    response.sendRedirect(request.getContextPath() + statusUrl);
	    return "";
	}
	
    private String goStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account account = AccountController.getCurrentAccount(request);
        if (isBallotSubmitted(account)) {
            request.setAttribute("ballotSubmitted", true);
        } else {
            String locationName = "";
            try {
                Location location = LocationDB.readId(account.getLocationId());
                locationName = location.getName();
            } catch (SQLException e) {
                locationName = "Invalid location";
            }
            request.setAttribute("locationName", locationName);
        }
        return "/views/ballotStatus.jsp";
    }

    private boolean isBallotSubmitted(Account account) {
        if (account != null) {
            Date ballotSubmissionDate = account.getVoteRecorded();
            return (ballotSubmissionDate != null);
        }
        return false;
    }
}
