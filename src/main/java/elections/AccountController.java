package elections;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import elections.data.AccountDB;
import elections.data.ResponseDB;
import elections.models.Account;

@WebServlet("/accounts/*")
public class AccountController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String adminUrl = "/accounts/admin";
	
	protected void doGet(
	        HttpServletRequest request,
	        HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = null;

        if (requestURI.endsWith("/sign-in")) {
            url = goSignIn(request, response, false);
        } else if (requestURI.endsWith("/sign-in-qr")) {
            url = goSignIn(request, response, true);
        } else if (requestURI.endsWith("/sign-out")) {
            url = goSignOut(request, response);
        } else if (requestURI.endsWith("/admin")) {
            url = goAdmin(request, response);
        } else if (requestURI.endsWith("/delete")) {
            url = goDelete(request, response);
        } else if (requestURI.endsWith("/delete-responses")) {
            url = goDeleteResponses(request, response);
        } else if (requestURI.endsWith("/add")) {
            url = goAdd(request, response);
        } else if (requestURI.endsWith("/edit")) {
            url = goEdit(request, response);
        } else if (requestURI.endsWith("/view-qr")) {
            url = goViewQr(request, response);
        }
        
        doRespond(request, response, url);
	}

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = null;

        if (requestURI.endsWith("/sign-in")) {
            url = goAuthenticate(request, response, false);
        } else if (requestURI.endsWith("/sign-in-qr")) {
            url = goAuthenticate(request, response, true);
        } else if (requestURI.endsWith("/add")) {
            url = goSendAccount(request, response, false);
        } else if (requestURI.endsWith("/edit")) {
            url = goSendAccount(request, response, true);
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

    private String goAdmin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (redirectRole(request, response, 1)) {
            return "";
        }
        
        request.setAttribute("navActiveAdmin", "active");
        try {
            ArrayList<Account> accounts = AccountDB.read();
            request.setAttribute("accounts", accounts);
        } catch (Exception e) {
            return null;
        }
        
        return "/views/accountAdmin.jsp";
    }

    private String goDelete(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (redirectRole(request, response, 1)) {
            return "";
        }

        int targetId = Integer.valueOf(request.getParameter("id"));
        try {
            ResponseDB.delete(targetId);
            AccountDB.delete(targetId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        response.sendRedirect(request.getContextPath() + adminUrl);
        return "";
    }

    private String goDeleteResponses(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (redirectRole(request, response, 1)) {
            return "";
        }
        
        try {
            ResponseDB.deleteAll();
            AccountDB.clearVoteState();
        } catch (Exception e) {
            return null;
        }

        response.sendRedirect(request.getContextPath() + adminUrl);
        return "";
    }

    private String goAdd(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (redirectRole(request, response, 1)) {
            return "";
        }

        request.setAttribute("navActiveAdmin", "active");
        return "/views/accountAddEdit.jsp";
    }

    private String goEdit(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (redirectRole(request, response, 1)) {
            return "";
        }

        request.setAttribute("navActiveAdmin", "active");
        request.setAttribute("isEdit", true);
        try {
            int accountId = Integer.valueOf(request.getParameter("id"));
            Account account = AccountDB.readId(accountId);
            
            request.setAttribute("nameFirst", account.getFirstName());
            request.setAttribute("nameMiddle", account.getMiddleName());
            request.setAttribute("nameLast", account.getLastName());
            request.setAttribute("nameSuffix", account.getSuffix());
            request.setAttribute("authUsername", account.getUsername());
            request.setAttribute("authEmail", account.getEmail());
            request.setAttribute("authRole", account.getRoleId());
            request.setAttribute("targetId", account.getId());
        } catch (Exception e) {
            return null;
        }
        
        return "/views/accountAddEdit.jsp";
    }

    private String goSendAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean isEdit)
            throws ServletException, IOException {
        Account account = new Account();
        try {
            if (isEdit) {
                int accountId = Integer.valueOf(request.getParameter("target-id"));
                account = AccountDB.readId(accountId);
            }

            account.setFirstName(request.getParameter("name-first"));
            account.setMiddleName(request.getParameter("name-middle"));
            account.setLastName(request.getParameter("name-last"));
            account.setSuffix(request.getParameter("name-suffix"));
            account.setEmail(request.getParameter("auth-email"));
            account.setRoleId(Integer.valueOf(request.getParameter("auth-role")));

            account.setUsername(request.getParameter("auth-username"));
            String password = request.getParameter("auth-password");
            // Encrypt the password if it is not empty
            if (StringUtils.isNotBlank(password)) {
                password = DigestUtils.sha256Hex(password);
                account.setPassword(password);
            }
            
            if (isEdit) {
                AccountDB.update(account);
            } else {
                account.randomizeUuid();
                AccountDB.create(account);
            }
        } catch (Exception e) {
            return null;
        }

        response.sendRedirect(request.getContextPath() + adminUrl);
        return "";
    }

    private String goViewQr(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (redirectRole(request, response, 0)) {
            return "";
        }
        
        request.setAttribute("navActiveViewQr", "active");

        return "/views/accountQr.jsp";
    }

    private String redirectStatus(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + BallotController.statusUrl);
        return "";
    }

    public static boolean redirectRole(
            HttpServletRequest request,
            HttpServletResponse response,
            int requiredRole)
            throws ServletException, IOException {
        Account account = getCurrentAccount(request);
        if (account == null || account.getRoleId() < requiredRole) {
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
}
