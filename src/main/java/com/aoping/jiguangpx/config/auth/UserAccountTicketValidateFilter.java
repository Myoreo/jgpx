package com.aoping.jiguangpx.config.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aoping.jiguangpx.constant.RedisKey;
import com.aoping.jiguangpx.entity.UserAccount;
import com.aoping.jiguangpx.util.HttpUtil;

@Service
public class UserAccountTicketValidateFilter extends OncePerRequestFilter {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ((authentication == null || !authentication.isAuthenticated())) {

			String ticket = HttpUtil.getCookie(request, "jgpx_ticket");
			String idStr = StringUtils.isNotBlank(ticket)
					? stringRedisTemplate.opsForValue().get(RedisKey.Account.ticket2Account(ticket))
					: null;

			if (StringUtils.isNotBlank(idStr)) {
				List<SimpleGrantedAuthority> authorities = new ArrayList<>();
				
				UserAccount account = new UserAccount();
				account.setId(Integer.valueOf(idStr));
				AuthUser authUser = new AuthUser(idStr, "[PROTECTED]", authorities);
				authUser.setLoginType(LoginType.PASSWORD);
				authUser.setAccount(account);
				
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authUser, null,
						authorities);
				token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(token);
			}
		}

		filterChain.doFilter(request, response);
		
	}

}
