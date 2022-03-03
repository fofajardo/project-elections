package elections;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

import elections.models.*;
import elections.data.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class AuthManager {
    public static boolean redirectGuest(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object accountId = session.getAttribute("accountId");
        if (accountId == null) {
            response.sendRedirect(request.getContextPath());
            return true;
        }
        return false;
    }

    public static Account getCurrentAccount(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object accountId = session.getAttribute("accountId");
        if (accountId != null) {
            try {
                Account account = AccountDB.readId((int)accountId);
                return account;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static boolean signIn(
            HttpServletRequest request,
            HttpServletResponse response,
            String emailOrUsername,
            String password)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession();
        Account account = AccountDB.readCredentials(emailOrUsername, password);
        if (account != null) {
            account.setLastSignIn(Date.from(Instant.now()));
            try {
                AccountDB.update(account);
            } catch (SQLException e) {
                return false;
            }
            session.setAttribute("accountId", account.getId());
            return true;
        }
        return false;
    }
    
    public static void signOut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("accountId");
    }
}
