package elections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
                ArrayList<Candidate> data = CandidateDB.readFromPositionWithVotes(positionId);
                candidates.put(positionId, data);
            }
            request.setAttribute("candidates", candidates);
            
            //
            ArrayList<Party> partylists = PartyDB.readPartylistWithVotes();
            request.setAttribute("partylists", partylists);
            
            request.setAttribute("retrieval", new Date());
        } catch (Exception e) {
            return null; 
        }

        request.setAttribute("isResults", true);
        return "/views/ballotView.jsp";
    }

}
