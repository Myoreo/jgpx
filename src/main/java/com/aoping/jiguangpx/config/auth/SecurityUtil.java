package com.aoping.jiguangpx.config.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtil {
	
	public static int getAccountId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			AuthUser user = (AuthUser) authentication.getPrincipal();
			return user.getAccount().getId();
		}
		throw new RuntimeException("未登录");
	}

}
