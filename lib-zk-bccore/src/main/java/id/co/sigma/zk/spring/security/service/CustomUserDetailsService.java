/**
 * 
 */
package id.co.sigma.zk.spring.security.service;

import id.co.sigma.common.security.domain.User;
import id.co.sigma.security.server.dao.impl.UserDaoImpl;
import id.co.sigma.zk.spring.security.model.UserData;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

	private static final Map<String, UserData> USERS = new HashMap<String,UserData>();
	private static void add(UserData mu){
		USERS.put(mu.getUsername(), mu);
	}
	
//	static{
//		
//		add(new UserData("rod","81dc9bdb52d04dc20036dbd8313ed055", 
//				new String[]{"ROLE_USER", "ROLE_EDITOR"} ));//1234
//		
//		add(new UserData("dianne","65d15fe9156f9c4bbffd98085992a44e", 
//				new String[]{"ROLE_USER", "ROLE_EDITOR"} ));//emu
//		
//		add(new UserData("scott","65d15fe9156f9c4bbffd98085992a44e", 
//				new String[]{"ROLE_USER"} ));//emu
//		
//		add(new UserData("peter","65d15fe9156f9c4bbffd98085992a44e", 
//				new String[]{"ROLE_USER"} ));//emu
//		
//	}

	@Autowired
	private UserDaoImpl userDao;
	
	// must return a value or throw UsernameNotFoundException
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		UserData user = USERS.get(username);
		if(user==null){
			//load from database
			User dbUser = userDao.getUserByUserName(username);
			if(dbUser != null) {
				user = new UserData(username, dbUser.getChipperText(), "ROLE_USER");
				user.setApplicationUser(dbUser);
				add(user);
			} else {
				throw new UsernameNotFoundException("cannot found user: "+username);
			}
		}
		return user;
	}


}
