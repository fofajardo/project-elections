package elections;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import elections.data.AccountDB;
import elections.models.Account;

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
        
        doRespond(request, response, url);
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
        
        doRespond(request, response, url);
	}

    private void doRespond(
            HttpServletRequest request,
            HttpServletResponse response,
            String url)
            throws ServletException, IOException {
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
        Account account = getCurrentAccount(request);
        if (account != null) {
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
        HttpSession session = request.getSession();
        session.removeAttribute("accountId");
        response.sendRedirect(request.getContextPath());
        return "";
    }

    private String goAuthenticate(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean useQr)
            throws ServletException, IOException {
        Account account = null;

        try {
            if (useQr) {
                String uuid = request.getParameter("auth-uuid");
                if (StringUtils.isNotBlank(uuid)) {
                    account = AccountDB.readUuid(uuid);
                }
                request.setAttribute("useQr", true);;
            } else {
                String emailOrUsername = request.getParameter("auth-emailOrUsername");
                String password = request.getParameter("auth-password");
                if (StringUtils.isNotBlank(emailOrUsername)
                        || StringUtils.isNotBlank(password)) {
                    account = AccountDB.readCredentials(emailOrUsername, password);
                }
            }

            if (account != null) {
                HttpSession session = request.getSession();
                account.setLastSignIn(Date.from(Instant.now()));
                AccountDB.update(account);
                session.setAttribute("accountId", account.getId());
                return redirectStatus(request, response);
            }
        } catch (Exception e) {
            return null;
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

    public static boolean redirectGuest(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        Account account = getCurrentAccount(request);
        if (account == null) {
            response.sendRedirect(request.getContextPath());
            return true;
        }
        return false;
    }

    public static Account getCurrentAccount(HttpServletRequest request) {
        Object account = request.getAttribute("account");
        if (account != null && account.getClass() == Account.class) {
            return (Account)account;
        }
        return null;
    }

    public static boolean isBallotSubmitted(HttpServletRequest request) {
        Account account = getCurrentAccount(request);
        if (account != null) {
            Date ballotSubmissionDate = account.getVoteRecorded();
            return (ballotSubmissionDate != null);
        }
        return false;
    }
}
