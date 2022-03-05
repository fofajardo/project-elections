package elections;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/accounts/*")
public class AccountController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(
	        HttpServletRequest request,
	        HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = null;

        if (requestURI.endsWith("sign-in")) {
            url = goSignIn(request, response, false);
        } else if (requestURI.endsWith("sign-in-qr")) {
            url = goSignIn(request, response, true);
        } else if (requestURI.endsWith("sign-out")) {
            url = goSignOut(request, response);
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
        String requestURI = request.getRequestURI();
        String url = null;

        if (requestURI.endsWith("sign-in")) {
            url = goAuthenticate(request, response, false);
        } else if (requestURI.endsWith("sign-in-qr")) {
            url = goAuthenticate(request, response, true);
        }
        
        if (url == null) {
            response.sendError(404);
        } else if (!url.isBlank()) {
            getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
        }
	}

    private String goSignIn(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean useQr)
            throws ServletException, IOException {
        if (AuthManager.getCurrentAccount(request, response) != null) {
            return redirectStatus(request, response);
        }
        if (useQr) {
            request.setAttribute("useQr", true);
        }
        return "/views/accountSignIn.jsp";
    }

    private String goSignOut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        AuthManager.signOut(request, response);
        response.sendRedirect(request.getContextPath());
        return "";
    }

    private String goAuthenticate(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean useQr)
            throws ServletException, IOException {
        boolean isAuthenticated = false;

        try {
            if (useQr) {
                String Uuid = request.getParameter("auth-uuid");
                isAuthenticated = AuthManager.signInByUuid(request, response, Uuid);
                request.setAttribute("useQr", true);;
            } else {
                String emailOrUsername = request.getParameter("auth-emailOrUsername");
                String password = request.getParameter("auth-password");
                isAuthenticated = AuthManager.signIn(request, response, emailOrUsername, password);
            }
        } catch (Exception e) {
            isAuthenticated = false;
        }

        if (isAuthenticated) {
            return redirectStatus(request, response);
        }

        request.setAttribute("authInvalid", true);
        return "/views/accountSignIn.jsp";
    }

    private String redirectStatus(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + BallotController.statusUrl);
        return "";
    }
}
