/**
 * 
 */
package id.co.sigma.zk.spring.security.service;

import id.co.sigma.common.security.domain.User;
import id.co.sigma.security.server.dao.impl.UserDaoImpl;
import id.co.sigma.zk.spring.security.model.UserData;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

	
	

	@Autowired
	private UserDaoImpl userDao;
	
	// must return a value or throw UsernameNotFoundException
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		UserData user = null; 

			User dbUser = userDao.getUserByUserName(username);
			if(dbUser != null) {
				dbUser.getBranch();
				String[] roles = userDao.getUserRoles(dbUser.getId());
				user = new UserData(username, dbUser.getChipperText(), getRoles(roles));
				user.setApplicationUser(dbUser);
				
			} else {
				throw new UsernameNotFoundException("cannot found user: "+username);
			}

		return user;
	}

	private String[] getRoles(String... roles) {
		String[] defRoles = new String[]{"ROLE_USER"};
		if(roles != null && roles.length > 0) {
			defRoles = new String[roles.length+1];
			defRoles[0] = "ROLE_USER";
			System.arraycopy(roles, 0, defRoles, 1, roles.length);
		}
		return defRoles;
	}

}
