package id.co.sigma.zk.spring.security;

import id.co.sigma.common.security.domain.Signon;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.server.service.IGeneralPurposeService;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextHolder;

public class CustomUrlAuthenticationSuccessHandler extends
		SimpleUrlAuthenticationSuccessHandler {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CustomUrlAuthenticationSuccessHandler.class);
			
	@Autowired
	protected IGeneralPurposeService generalPurposeService ;  
	
	
	public CustomUrlAuthenticationSuccessHandler() {
		super();
	}

	public CustomUrlAuthenticationSuccessHandler(String defaultTargetUrl) {
		super(defaultTargetUrl);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		super.onAuthenticationSuccess(request, response, authentication);
		
		
		String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
		
		User appUser = SecurityUtil.getUser().getApplicationUser();
		
		Signon signon = new Signon();
		signon.setLogonTime(new Date());
		signon.setUserId(appUser.getId());
		signon.setTerminal(request.getRemoteAddr());
		signon.setUserBrowser(getUserBrowser(request.getHeader("User-Agent")));
		signon.setUuid(sessionId);
		
		try {
			generalPurposeService.insert(signon);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}

	private String getUserBrowser(String userAgent) {
		String uAgent = userAgent.toLowerCase();
		if(uAgent.contains("chrome")) {
			return "Chrome";
		} else if (uAgent.contains("firefox")) {
			return "Mozilla Firefox";
		} else if (uAgent.contains("msie")) {
			return "Microsoft IE";
		}		 
		return "Others";
	}
}
