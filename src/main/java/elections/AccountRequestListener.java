/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package elections;

import elections.data.AccountDao;
import elections.models.Account;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * This request listener processes all requests and sets the attributes
 * for checking if an account is already signed in and if
 * they have submitted a ballot.
 */
@WebListener
public class AccountRequestListener implements ServletRequestListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestInitialized(ServletRequestEvent event) {
        HttpServletRequest request = (HttpServletRequest)event.getServletRequest();
        HttpSession session = request.getSession();
        // Get the account identifier
        Object accountId = session.getAttribute("accountId");
        if (accountId != null) {
            // Try to get the associated account and their ballot
            // submission status, then store them as attributes
            try {
                Account account = AccountDao.findById((int)accountId);
                request.setAttribute("account", account);
                request.setAttribute("ballotSubmitted", account.getVoteRecorded());
            } catch (Exception e) {
                request.setAttribute("account", null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        // There's nothing to do here.
    }
}
