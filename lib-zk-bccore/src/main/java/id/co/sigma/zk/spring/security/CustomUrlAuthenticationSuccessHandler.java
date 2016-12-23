package id.co.sigma.zk.spring.security;

import id.co.sigma.common.security.domain.Signon;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.server.service.IGeneralPurposeService;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.zkoss.web.Attributes;

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
		String localeCode = appUser.getLocale(); 
		if ( localeCode== null)
			localeCode = "id" ; 
		Locale locale = new Locale("id");
		
		request.getSession().setAttribute(Attributes.PREFERRED_LOCALE, locale);
		
		
		
		Signon signon = new Signon();
		signon.setLogonTime(new Date());
		signon.setUserId(appUser.getId());
		
		Enumeration<String> header = request.getHeaderNames();
		
		String ip = request.getHeader("X-Forwarded-For");
		String ip1 = request.getHeader("Proxy-Client-IP");
		String ip2 = request.getHeader("WL-Proxy-Client-IP");
		String ip3 = request.getHeader("HTTP_CLIENT_IP");
		String ip4 = request.getHeader("HTTP_X_FORWARDED_FOR");
		String ip5 = request.getHeader("getRemoteAddr");
		
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
            logger.debug("ngambil dari : Proxy-Client-IP");
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
            logger.debug("ngambil dari : WL-Proxy-Client-IP");
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
            logger.debug("ngambil dari : HTTP_CLIENT_IP");
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            logger.debug("ngambil dari : HTTP_X_FORWARDED_FOR");
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
            logger.debug("ngambil dari : getRemoteAddr");
        }  
        
        logger.debug(ip1+" ngambil dari : Proxy-Client-IP");
        logger.debug(ip2+" ngambil dari : WL-Proxy-Client-IP");
        logger.debug(ip3+" ngambil dari : HTTP_CLIENT_IP");
        logger.debug(ip4+" ngambil dari : HTTP_X_FORWARDED_FOR");
        logger.debug(ip5+" ngambil dari : getRemoteAddr");
		
		signon.setTerminal(ip);
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
