package id.co.sigma.zk.spring.security;

import id.co.sigma.common.data.app.SystemSimpleParameter;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.controller.security.ActiveSessionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;

public class ConfigurableConcurrentSessionControlAuthenticationStrategy extends ConcurrentSessionControlAuthenticationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ActiveSessionController.class);

    private static final String SECURITY_MAXIMUM_SESSION = "SECURITY_MAXIMUM_SESSION";

    @Autowired
    public ConfigurableConcurrentSessionControlAuthenticationStrategy(SessionRegistry sessionRegistry,
                                                                      IGeneralPurposeDao generalPurposeDao,
                                                                      IGeneralPurposeService generalPurposeService) {
        super(sessionRegistry);
        int maximumSessions = 1;
        try {
            SystemSimpleParameter parameter = generalPurposeDao.get(SystemSimpleParameter.class, SECURITY_MAXIMUM_SESSION);
            if (parameter == null) {
                parameter = new SystemSimpleParameter();
                parameter.setId(SECURITY_MAXIMUM_SESSION);
                parameter.setEditableFlag("No");
                parameter.setParamType("java.lang.Integer");
                parameter.setRemark("Security maximum session allowed per user");
                parameter.setValueRaw(String.valueOf(maximumSessions));
                generalPurposeService.insert(parameter);
            } else {
                maximumSessions = Integer.valueOf(parameter.getValueRaw());
            }
            this.setMaximumSessions(maximumSessions);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}