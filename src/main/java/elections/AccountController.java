package elections;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import elections.data.AccountDao;
import elections.data.ResponseDao;
import elections.models.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * This servlet processes all requests for /accounts.
 */
@WebServlet("/accounts/*")
public class AccountController extends HttpServlet {
    private static final long serialVersionUID = -2033261417270752539L;

    /**
     * Path to the sign in view.
     */
    private static final String VIEW_SIGN_IN = "/views/accountSignIn.jsp";
    /**
     * Path to the account QR code view.
     */
    private static final String VIEW_QR = "/views/accountQr.jsp";
    /**
     * Path to the administration view.
     */
    private static final String VIEW_ADMIN = "/views/accountAdmin.jsp";
    /**
     * Path to the add/edit account view.
     */
    private static final String VIEW_ADD_EDIT = "/views/accountAddEdit.jsp";

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
        switch (pathInfo) {
        case "/sign-in":
            // All: sign in
            url = goSignIn(request, response, false);
            break;
        case "/sign-in-qr":
            // All: sign in via QR
            url = goSignIn(request, response, true);
            break;
        case "/sign-out":
            // All: sign out
            url = goSignOut(request, response);
            break;
        case "/view-qr":
            // SI: view QR code for current account
            url = goViewQr(request, response);
            break;
        case "/admin":
            // Admin: main
            url = goAdmin(request, response);
            break;
        case "/add":
            // Admin: add new account
            url = goAdd(request, response);
            break;
        case "/edit":
            // Admin: edit account
            url = goEdit(request, response);
            break;
        case "/delete":
            // Admin: delete account
            url = goDelete(request, response);
            break;
        case "/delete-responses":
            // Account: delete all responses
            url = goDeleteResponses(request, response);
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
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Get path information and remove leading forward slash
        String pathInfo = StringUtils.stripEnd(request.getPathInfo(), "/");
        String url = null;

        // Determine how to handle the current path
        switch (pathInfo) {
        case "/sign-in":
            // All: sign in - authenticate credentials
            url = goAuthenticate(request, response, false);
            break;
        case "/sign-in-qr":
            // All: sign in via QR - authenticate credentials
            url = goAuthenticate(request, response, true);
            break;
        case "/add":
            // Admin: process information from add account
            url = goSendAccount(request, response, false);
            break;
        case "/edit":
            // Admin: process information from edit account
            url = goSendAccount(request, response, true);
            break;
        default:
            break;
        }

        ControllerUtils.respond(getServletContext(), request, response, url);
    }

    /**
     * This method handles the sign in page (email and QR).
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @param useQr the {@code boolean} determining if the sign in view
     *              should show the QR variant
     * @return a path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    private String goSignIn(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean useQr)
                    throws ServletException, IOException {
        // Redirect to the ballot status page if an account is signed in
        if (ControllerUtils.isSignedIn(request)) {
            return ControllerUtils.redirectStatus(request, response);
        }
        // Set the attribute for determining the type of sign in page
        request.setAttribute("useQr", useQr);
        // Return the target view path
        return VIEW_SIGN_IN;
    }

    /**
     * This method handles the sign out request.
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
    private String goSignOut(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Get the current session
        HttpSession session = request.getSession();
        // Remove the account identifier attribute
        session.removeAttribute("accountId");
        // Send a redirect back to the home page
        return ControllerUtils.redirectRoot(request, response);
    }

    /**
     * This method handles the POST request sent from the sign in page and
     * authenticates the provided credentials.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @param useQr the {@code boolean} determining if the request was sent
     *              from the sign in via QR page
     * @return a path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    private String goAuthenticate(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean useQr)
                    throws ServletException, IOException {
        Account account = null;
        try {
            // Determine if QR code data will be used for authentication
            if (useQr) {
                // Get the UUID parameter
                String uuid = request.getParameter("auth-uuid");
                // Try to find the account associated with the UUID
                if (StringUtils.isNotBlank(uuid)) {
                    account = AccountDao.findByUuid(uuid);
                }
                // Set the attribute that the request came from the
                // sign in via QR page. This is used to make sure that
                // the sign in via QR page will still be shown although
                // there was no account matched for the UUID
                request.setAttribute("useQr", true);
            } else {
                // Get the email/username and password parameters
                String emailOrUsername =
                        request.getParameter("auth-emailOrUsername");
                String password =
                        request.getParameter("auth-password");
                // Try to find the account associated with the email/username
                if (StringUtils.isNotBlank(emailOrUsername)
                        || StringUtils.isNotBlank(password)) {
                    account = AccountDao.findByEmailOrUsername(emailOrUsername);
                    // Compare the SHA-256 hashed password against the value
                    // stored in the database if it's a match. Otherwise,
                    // remove the value of the account variable
                    if (account != null) {
                        String hashedPassword = DigestUtils.sha256Hex(password);
                        if (!account.getPassword().equals(hashedPassword)) {
                            account = null;
                        }
                    }
                }
            }

            // An account was matched
            if (account != null) {
                // Update the last signed in date and time for this account
                account.setLastSignIn(Date.from(Instant.now()));
                AccountDao.update(account);
                // Preserve the associated account identifier for this session
                HttpSession session = request.getSession();
                session.setAttribute("accountId", account.getId());
                // Redirect to the ballot status page
                return ControllerUtils.redirectStatus(request, response);
            }
        } catch (Exception e) {
            // Ignore the caught exception
            return null;
        }

        // Tell the sign in view that the authentication is invalid
        request.setAttribute("authInvalid", true);
        // Return the target view path
        return VIEW_SIGN_IN;
    }

    /**
     * This method handles the view QR page.
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
    private String goViewQr(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Redirect to home page if there is no signed in account
        if (ControllerUtils.redirectRole(request, response, 0)) {
            return "";
        }
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "ViewQr");
        // Return the target view path
        return VIEW_QR;
    }

    /**
     * This method handles the administration page.
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
    private String goAdmin(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Redirect to home page if there is no signed in account
        // or if the current account is not an admin
        if (ControllerUtils.redirectRole(request, response, 1)) {
            return "";
        }
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Admin");
        // Retrieve all accounts and store the list as an attribute
        try {
            List<Account> accounts = AccountDao.findAll();
            request.setAttribute("accounts", accounts);
        } catch (Exception e) {
            // Ignore the caught exception
            return null;
        }
        // Return the target view path
        return VIEW_ADMIN;
    }

    /**
     * This method handles the request to delete an account.
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
    private String goDelete(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Redirect to home page if there is no signed in account
        // or if the current account is not an admin
        if (ControllerUtils.redirectRole(request, response, 1)) {
            return "";
        }
        // Get the account identifier parameter
        int targetId = Integer.valueOf(request.getParameter("id"));
        // Try to delete the account using the given identifier
        try {
            ResponseDao.delete(targetId);
            AccountDao.delete(targetId);
        } catch (Exception e) {
            // Ignore the caught exception
            return null;
        }
        // Redirect to the admin page
        return ControllerUtils.redirectAdmin(request, response);
    }

    /**
     * This method handles the request to delete all responses.
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
    private String goDeleteResponses(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Redirect to home page if there is no signed in account
        // or if the current account is not an admin
        if (ControllerUtils.redirectRole(request, response, 1)) {
            return "";
        }
        // Try to delete all responses and remove all vote recorded timestamps
        try {
            ResponseDao.deleteAll();
            AccountDao.resetAllVoteRecorded();
        } catch (Exception e) {
            // Ignore the caught exception
            return null;
        }
        // Redirect to the admin page
        return ControllerUtils.redirectAdmin(request, response);
    }

    /**
     * This method handles the account add page.
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
    private String goAdd(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Redirect to home page if there is no signed in account
        // or if the current account is not an admin
        if (ControllerUtils.redirectRole(request, response, 1)) {
            return "";
        }
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Admin");
        // Return the target view path
        return VIEW_ADD_EDIT;
    }

    /**
     * This method handles the account edit page.
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
    private String goEdit(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Redirect to home page if there is no signed in account
        // or if the current account is not an admin
        if (ControllerUtils.redirectRole(request, response, 1)) {
            return "";
        }
        // Set the active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Admin");
        // Tell the add/edit account view that this is an edit page
        request.setAttribute("isEdit", true);
        try {
            // Try to get the account using the given identifier
            int accountId = Integer.valueOf(request.getParameter("id"));
            Account account = AccountDao.findById(accountId);
            // Populate the view with the account details
            request.setAttribute("nameFirst", account.getFirstName());
            request.setAttribute("nameMiddle", account.getMiddleName());
            request.setAttribute("nameLast", account.getLastName());
            request.setAttribute("nameSuffix", account.getSuffix());
            request.setAttribute("authUsername", account.getUsername());
            request.setAttribute("authEmail", account.getEmail());
            request.setAttribute("authRole", account.getRoleId());
            request.setAttribute("targetId", account.getId());
        } catch (Exception e) {
            // Ignore the caught exception
            return null;
        }
        // Return target view path
        return VIEW_ADD_EDIT;
    }

    /**
     * This method handles POST requests sent from the add/edit account pages.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @param isEdit a {@code boolean} determining whether the request was
     *               sent from the edit account page
     * @return a path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    private String goSendAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean isEdit)
                    throws ServletException, IOException {
        // Initialize an empty instance of the account model
        Account account = new Account();
        try {
            // Try to get the account using the given identifier if the
            // request comes from the account edit page
            if (isEdit) {
                int accountId = Integer.valueOf(request.getParameter("target-id"));
                account = AccountDao.findById(accountId);
            }
            // Populate the account object with values from
            // the specified parameters
            account.setFirstName(request.getParameter("name-first"));
            account.setMiddleName(request.getParameter("name-middle"));
            account.setLastName(request.getParameter("name-last"));
            account.setSuffix(request.getParameter("name-suffix"));
            account.setEmail(request.getParameter("auth-email"));
            account.setRoleId(Integer.valueOf(request.getParameter("auth-role")));
            account.setUsername(request.getParameter("auth-username"));

            String password = request.getParameter("auth-password");
            // Get the SHA-256 hash of the password if it is not empty
            if (StringUtils.isNotBlank(password)) {
                password = DigestUtils.sha256Hex(password);
                account.setPassword(password);
            }

            // Determine if an update or create operation should be performed
            // depending on from where the request was sent
            if (isEdit) {
                AccountDao.update(account);
            } else {
                // Randomize the new account's UUID which will be used
                // for generating the sign in QR code
                account.randomizeUuid();
                AccountDao.create(account);
            }
        } catch (Exception e) {
            // Ignore the caught exception
            return null;
        }
        // Redirect to the admin page
        return ControllerUtils.redirectAdmin(request, response);
    }
}
