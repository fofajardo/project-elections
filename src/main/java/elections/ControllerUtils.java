/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections;

import java.io.IOException;

import elections.models.Account;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This class defines static methods for use by the controller classes.
 */
public class ControllerUtils {
    /**
     * Path to the ballot status page.
     */
    private static final String URL_STATUS = "/ballot/status";
    /**
     * Path to the main admin page.
     */
    private static final String URL_ADMIN = "/accounts/admin";

    /**
     * This method determines the response which will be sent to the client.
     * <p>
     * There are three (3) possible outcomes when this method is called:
     * (a) an error response with the 404 status code will be sent;
     * (b) the request will be forwarded to the specified resource, which
     *     is the value of the {@code url} parameter;
     * (c) no response will be sent (i.e., when sending a redirect).
     *
     * @param context the {@link ServletContext} of the servlet
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @param url the path name to the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    public static void respond(ServletContext context,
            HttpServletRequest request, HttpServletResponse response,
            String url)
                    throws ServletException, IOException {
        if (url == null) {
            response.sendError(404);
            return;
        }

        if (!url.isBlank()) {
            context.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * Returns an empty string and sends a redirect response with
     * the default (root) context as the URL.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @return an empty string
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    public static String redirectRoot(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        response.sendRedirect(request.getContextPath());
        return "";
    }

    /**
     * Returns an empty string and sends a redirect response with
     * the status page as the URL.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @return an empty string
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    public static String redirectStatus(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + URL_STATUS);
        return "";
    }

    /**
     * Returns an empty string and sends a redirect response with
     * the administration as the URL.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @return an empty string
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    public static String redirectAdmin(
            HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + URL_ADMIN);
        return "";
    }

    /**
     * Returns whether the current account matches the specified role and
     * if {@code true}, sends a redirect response with the
     * default (root) context as the URL
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains
     *                 the response the servlet sends to the client
     * @param requiredRole the minimum required role
     * @return a {@code boolean} determining whether a redirect
     *         response was sent
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the
     *                     servlet handles the request
     */
    public static boolean redirectRole(
            HttpServletRequest request,
            HttpServletResponse response,
            int requiredRole)
                    throws ServletException, IOException {
        Account account = getCurrentAccount(request);
        // Check if an account is signed in or if the role is matched
        if (account == null || account.getRoleId() < requiredRole) {
            response.sendRedirect(request.getContextPath());
            return true;
        }
        return false;
    }

    /**
     * Returns the signed in account for this session.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @return an {@link Account} object representing the
     *         currently signed in account
     */
    public static Account getCurrentAccount(HttpServletRequest request) {
        Object account = request.getAttribute("account");
        if (account != null && account.getClass() == Account.class) {
            return (Account)account;
        }
        return null;
    }

    /**
     * Returns whether an account is signed in for this session.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @return a {@code boolean} determining whether an account
     *         is signed in for this session
     */
    public static boolean isSignedIn(HttpServletRequest request) {
        return (getCurrentAccount(request) != null);
    }

    /**
     * Sets the attribute determining the active page in
     * the sidebar navigation.
     *
     * @param request the {@link HttpServletRequest} object that contains
     *                the request the client has made of the servlet
     * @param pageAlias
     * @apiNote This is linked to the {@code header} JSP fragment.
     */
    public static void setActiveSidebarPage(HttpServletRequest request,
            String pageAlias) {
        request.setAttribute("navActive" + pageAlias, "active");
    }
}
