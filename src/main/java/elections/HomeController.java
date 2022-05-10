/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This servlet processes requests for the default (root) context and /home.
 */
@WebServlet(urlPatterns = { "", "/home" })
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = -5213345009456204126L;

    /**
     * Path to the home view.
     */
    private static final String VIEW_HOME = "/views/home.jsp";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        // Set active sidebar page
        ControllerUtils.setActiveSidebarPage(request, "Home");
        // Respond with target view path
        ControllerUtils.respond(getServletContext(), request, response, VIEW_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
        doGet(request, response);
    }
}
