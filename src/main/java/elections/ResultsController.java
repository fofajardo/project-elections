package elections;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.github.cliftonlabs.json_simple.JsonObject;
import elections.data.*;
import elections.models.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/results/*")
public class ResultsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = null;

        if (requestURI.endsWith("/view")) {
            url = goView(request, response);
        } else if (requestURI.endsWith("/get")) {
            url = goSendJson(request, response);
        }
        
        if (url == null) {
            response.sendError(404);
        } else if (!url.isBlank()) {
            getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
        }
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String goView(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("navActiveResults", "active");
        try {
            ArrayList<Position> positions = PositionDB.read();
            request.setAttribute("positions",  positions);

            HashMap<Integer, ArrayList<Candidate>> candidates = new HashMap<>();
            for (int i = 0; i < positions.size(); i++) {
                int positionId = positions.get(i).getId();
                ArrayList<Candidate> data = CandidateDB.readFromPositionWithVotes(positionId, 0);
                candidates.put(positionId, data);
            }
            request.setAttribute("candidates", candidates);
            
            //
            ArrayList<Party> partylists = PartyDB.readPartylistWithVotes(0);
            request.setAttribute("partylists", partylists);
            
            request.setAttribute("retrieval", new Date());
        } catch (Exception e) {
            return null; 
        }

        request.setAttribute("isResults", true);
        return "/views/ballotView.jsp";
    }

    private String goSendJson(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        JsonObject results = new JsonObject();

        try {
            ArrayList<Position> positions = PositionDB.read();
            for (int i = 0; i < positions.size(); i++) {
                int positionId = positions.get(i).getId();
                ArrayList<Candidate> data = CandidateDB.readFromPositionWithVotes(positionId, 12);
                
                ArrayList<String> graphLabels = new ArrayList<>();
                ArrayList<Integer> graphData = new ArrayList<>();
                for (int j = 0; j < data.size(); j++) {
                    Candidate candidate = data.get(j);
                    String fullName = String.format(
                            "%s, %s %s %s",
                            candidate.getLastName(),
                            candidate.getFirstName(),
                            candidate.getMiddleName(),
                            candidate.getSuffix()).trim();
                    graphLabels.add(fullName);
                    graphData.add(candidate.getVotes());
                }
                
                JsonObject positionJson = new JsonObject();
                positionJson.put("labels", graphLabels);
                positionJson.put("data", graphData);
                positionJson.put("id", positionId);

                results.put(String.valueOf(i), positionJson);
            }
        } catch (Exception e) {
            results.put("error", e.getMessage());
        }
        
        PrintWriter writer = response.getWriter();
        writer.print(results.toJson());
        
        return "";
    }
}
