package id.co.sigma.zk.ui.controller.security;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Records new session in the active session map and remove it when the session is expired.
 * This listener is utilized by the ActiveSessionController that enables admin to manage user's online session like
 * killing or force the user session to expired.
 */
public class ActiveSessionHttpSessionListener implements HttpSessionListener {
    public static Map<String, HttpSession> activeSession = new HashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeSession.put(se.getSession().getId(), se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeSession.remove(se.getSession().getId());
    }
}
