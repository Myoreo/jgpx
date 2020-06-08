package com.aoping.jiguangpx.config.auth;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aoping.jiguangpx.dao.UserAccountDao;
import com.aoping.jiguangpx.entity.UserAccount;

@Service
public class DefaultUserDetailsService implements UserDetailsService {

	@Autowired
	private UserAccountDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount account = userDao.getByPhone(username);
		if (account == null) {
			throw new UsernameNotFoundException("用户不存在");
		}
		// 用户权限 authorities
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		AuthUser authUser = new AuthUser(account.getPhone(), account.getPassword(), authorities);
		authUser.setLoginType(LoginType.PASSWORD);
		authUser.setAccount(account);
		
		return authUser;
	}
}
