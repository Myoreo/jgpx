package com.aoping.jiguangpx.config.auth.handler;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.aoping.jiguangpx.base.Result;
import com.aoping.jiguangpx.base.ResultUtil;
import com.aoping.jiguangpx.config.auth.AuthUser;
import com.aoping.jiguangpx.constant.RedisKey;
import com.aoping.jiguangpx.entity.UserAccount;
import com.aoping.jiguangpx.util.HttpUtil;
import com.aoping.jiguangpx.util.StringAddUtils;

@Component
public class DefaultAuthenticationSucessHandler implements AuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(DefaultAuthenticationSucessHandler.class);
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
		String remoteAddress = details.getRemoteAddress();
		AuthUser user = (AuthUser) authentication.getPrincipal();
		logger.info("login success principal:{},remoteAddress:{}", user, remoteAddress);
		String username = user.getUsername();
		
		String ticket = Base64.getEncoder().encodeToString(username.getBytes()) + StringAddUtils.RandomString(8);
		
		UserAccount account = user.getAccount();

		stringRedisTemplate.opsForValue().set(RedisKey.Account.ticket2Account(ticket), account.getId() + "", 3,
				TimeUnit.DAYS);
		HttpUtil.setCookie(response, "jgpx_ticket", ticket, null);

		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(JSON.toJSONString(ResultUtil.success("登录成功")));
	}

}
