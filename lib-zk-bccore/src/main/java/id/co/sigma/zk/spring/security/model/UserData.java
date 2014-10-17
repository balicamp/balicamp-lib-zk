package id.co.sigma.zk.spring.security.model;

import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.server.data.security.CoreComponentAuthority;
import id.co.sigma.common.server.data.security.SimpleUserData;

import java.util.ArrayList;
import java.util.Collection;

public class UserData extends SimpleUserData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String password;
	
	private User applicationUser;
	
	public UserData() {
		super();
	}
	
	public UserData(String username, String password) {
		setUsername(username);
		this.password = password;
	}

	public UserData(String username, String password, String... roles) {
		setUsername(username);
		this.password = password;
		setAuthorities(toGrantedAuthorities(roles));
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRoles(String... roles) {
		setAuthorities(toGrantedAuthorities(roles));
	}
	
	private static Collection<CoreComponentAuthority> toGrantedAuthorities(String... authoStrs){
		ArrayList<CoreComponentAuthority> arrList = new ArrayList<CoreComponentAuthority>(authoStrs.length);
		for(String gaStr : authoStrs){
			CoreComponentAuthority auth = new CoreComponentAuthority();
			auth.setAuthority(gaStr);
			arrList.add(auth);
		}
		return arrList;
	}

	public User getApplicationUser() {
		return applicationUser;
	}

	public void setApplicationUser(User applicationUser) {
		this.applicationUser = applicationUser;
	}
	
}
