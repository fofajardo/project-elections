package elections;

import elections.data.AccountDB;
import elections.models.Account;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@WebListener
public class AccountRequestListener implements ServletRequestListener {

    public void requestInitialized(ServletRequestEvent event) {
        HttpServletRequest request = (HttpServletRequest)event.getServletRequest();
        HttpSession session = request.getSession();
        Object accountId = session.getAttribute("accountId");
        if (accountId != null) {
            try {
                Account account = AccountDB.readId((int)accountId);
                request.setAttribute("account", account);
            } catch (Exception e) {
                request.setAttribute("account", null);
            }
        }
    }

    public void requestDestroyed(ServletRequestEvent event)  {
    }
}