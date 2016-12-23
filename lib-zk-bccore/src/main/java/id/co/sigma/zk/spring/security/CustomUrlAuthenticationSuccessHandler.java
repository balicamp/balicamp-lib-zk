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
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        
        System.out.println("Ip yg login : "+ip);
		
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
