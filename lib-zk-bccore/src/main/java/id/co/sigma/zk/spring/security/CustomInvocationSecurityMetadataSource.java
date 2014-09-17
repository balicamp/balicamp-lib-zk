package id.co.sigma.zk.spring.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class CustomInvocationSecurityMetadataSource implements
		FilterInvocationSecurityMetadataSource {
	
	private static final String ROLE_NONE = "ROLE_NONE";
	private static final String[] ANONYMOUS_RESOURCES = new String[]{
		".wpd", 
		".css", 
		".wcs",
		".gif",
		".jpg",
		".jpeg",
		".png",
		".bmp",
		".js",
		".less",
		".dsp"
		};
	
	Map<String, String> interceptPatternUrl = new HashMap<String, String>();
	Map<RequestMatcher, String> reqsRole = new HashMap<RequestMatcher, String>();

	public CustomInvocationSecurityMetadataSource(
			Map<String, String> interceptPatternUrl) {
		super();
		this.interceptPatternUrl = interceptPatternUrl;
		Set<String> iter = this.interceptPatternUrl.keySet();
		for(String key : iter) {
			reqsRole.put(new AntPathRequestMatcher(key), this.interceptPatternUrl.get(key));
		}
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object)
			throws IllegalArgumentException {
		
		FilterInvocation fi = (FilterInvocation) object;
		
		List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
		
		attributes = SecurityConfig.createList(getRoles(fi));
		
		return attributes;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

	private String[] getRoles(FilterInvocation fi) {
		List<String> roles = new ArrayList<String>();
		for(RequestMatcher req : this.reqsRole.keySet()) {
			if(req.matches(fi.getHttpRequest())) {
				roles.add(this.reqsRole.get(req));
			}
		}
		if((roles.size() == 0) && (!isAnonymousAccess(fi.getRequestUrl()))) {
			RequestMatcher key = new AntPathRequestMatcher(fi.getRequestUrl());
			this.reqsRole.put(key, ROLE_NONE);
			roles.add(this.reqsRole.get(key));
		}
		String[] strRoles = roles.toArray(new String[roles.size()]);
		return strRoles;
	}
	
	private boolean isAnonymousAccess(String url) {
		
		boolean skip = false;
		for(String res : ANONYMOUS_RESOURCES) {
			skip = skip || url.contains(res);
			if(skip) break;
		}
		
		return skip;
	}
}
