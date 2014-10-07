package id.co.sigma.zk.spring.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.security.domain.Signon;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.service.IGeneralPurposeService;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.context.request.RequestContextHolder;

public class SignonHistoryLogoutHandler implements LogoutHandler {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SignonHistoryLogoutHandler.class);
			
	@Autowired
	protected IGeneralPurposeDao  generalPurposeDao ; 
	
	@Autowired
	protected IGeneralPurposeService generalPurposeService ;  
	
	@Override
	public void logout(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) {
		
		if(SecurityUtil.getUser() == null) return;
		
		String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
		User user = SecurityUtil.getUser().getApplicationUser();
		
		try {
			
			List<Signon> list = generalPurposeDao.list(Signon.class, 
					new SimpleQueryFilter[]{
						new SimpleQueryFilter("userId", SimpleQueryFilterOperator.equal, user.getId()),
						new SimpleQueryFilter("uuid", SimpleQueryFilterOperator.equal, sessionId)
					}, null
			);
			
			if(!list.isEmpty()) {
				for(Signon hs : list) {
					hs.setLogoutTime(new Date());
					generalPurposeService.update(hs);
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}
