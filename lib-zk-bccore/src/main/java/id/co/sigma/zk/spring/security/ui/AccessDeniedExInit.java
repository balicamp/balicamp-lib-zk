/**
 * 
 */
package id.co.sigma.zk.spring.security.ui;

import id.co.sigma.zk.spring.security.SecurityUtil;

import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.GenericInitiator;

public class AccessDeniedExInit extends GenericInitiator {

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		if(SecurityUtil.isNoneGranted("ROLE_EDITOR")){
			throw new AccessDeniedException("this is a test of AccessDeniedException!");	
		}
	}
}
