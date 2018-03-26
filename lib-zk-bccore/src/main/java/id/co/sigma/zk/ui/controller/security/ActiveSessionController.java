package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.app.SystemSimpleParameter;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.spring.security.ConfigurableConcurrentSessionControlAuthenticationStrategy;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Component
public class ActiveSessionController extends BaseSimpleController {
    private static final Logger logger = LoggerFactory.getLogger(ActiveSessionController.class);

    private static final String SECURITY_MAXIMUM_SESSION = "SECURITY_MAXIMUM_SESSION";

    private EventQueue<Event> eventQueue = EventQueues.lookup("onSessionKilled", EventQueues.SESSION, true);

    @Wire
    private Spinner maxSessionSpinner;

    @Wire
    private Listbox activeSessionListbox;

    @Wire
    private Timer refreshTimer;

    @Autowired
    private IGeneralPurposeDao generalPurposeDao;

    @Autowired
    private IGeneralPurposeService generalPurposeService;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private ConfigurableConcurrentSessionControlAuthenticationStrategy authenticationStrategy;

    private SystemSimpleParameter getMaximumSessionsParameter() {
        try {
            return generalPurposeDao.get(SystemSimpleParameter.class, SECURITY_MAXIMUM_SESSION);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        try {
            eventQueue.subscribe(new EventListener<Event>() {
                @Override
                public void onEvent(Event event) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("ActiveSessionController.onEvent-x");
                    }
                    Set<Listitem> selectedItems = activeSessionListbox.getSelectedItems();
                    for (Listitem item : selectedItems) {
                        SessionInformation sessionInformation = item.getValue();
                        sessionInformation.expireNow();
                        sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());

                        HttpSession httpSession = ActiveSessionHttpSessionListener.activeSession.get(sessionInformation.getSessionId());
                        ActiveSessionHttpSessionListener.activeSession.remove(sessionInformation.getSessionId());
                        if (httpSession != null) {
                            httpSession.invalidate();
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        int maximumSessions = 0;
        SystemSimpleParameter maximumSessionsParameter = getMaximumSessionsParameter();
        if (maximumSessionsParameter != null) {
            maximumSessions = Integer.valueOf(maximumSessionsParameter.getValueRaw());
        }
        maxSessionSpinner.setValue(maximumSessions);
        refreshActiveSession();
    }

    @Listen("onClick = #saveButton")
    public void onClickSaveButton() {
        SystemSimpleParameter maximumSessionsParameter = getMaximumSessionsParameter();
        if (maximumSessionsParameter != null) {
            maximumSessionsParameter.setValueRaw(String.valueOf(maxSessionSpinner.getValue()));

            try {
                generalPurposeService.update(maximumSessionsParameter);
                authenticationStrategy.setMaximumSessions(maxSessionSpinner.getValue());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Listen("onClick = #killSessionButton")
    public void onKillSession() {
        if (logger.isDebugEnabled()) {
            logger.debug("ActiveSessionController.onKillSession");
        }
        Messagebox.show("Apakah anda yakin akan menutup session user?",
                "Konfirmasi", new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO},
                new String[]{"Ya", "Tidak"}, Messagebox.QUESTION, Messagebox.Button.NO,
                new EventListener<Messagebox.ClickEvent>() {
                    @Override
                    public void onEvent(Messagebox.ClickEvent event) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("ActiveSessionController.onEvent-y");
                        }
                        if (event.getButton() == Messagebox.Button.YES) {

                            // It is important to publish event in a new process thread!
                            Thread processThread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        eventQueue.publish(new Event("onSessionKilled", null, null));
                                    } catch (Exception e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            };
                            processThread.start();

                            try {
                                // Wait to make the eventQueue.subscribe() executed first,
                                // then we refresh the UI using onTimer event.
                                Thread.sleep(1000);
                                refreshTimer.start();
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                });
    }

    @Listen("onClick = #refreshButton; onTimer = #refreshTimer")
    public void refreshActiveSession() {
        if (logger.isDebugEnabled()) {
            logger.debug("ActiveSessionController.refreshActiveSession");
        }
        List<SessionInformation> sessionInformations = new ArrayList<>();
        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals) {
            sessionInformations.addAll(sessionRegistry.getAllSessions(principal, false));
        }
        ListModelList<SessionInformation> model = new ListModelList<>(sessionInformations);
        model.setMultiple(true);
        activeSessionListbox.setModel(model);
    }
}
