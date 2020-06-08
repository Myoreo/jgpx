package com.aoping.jiguangpx.config.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.aoping.jiguangpx.base.Result;
import com.aoping.jiguangpx.base.ResultUtil;

@Component
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String message;
		if (exception instanceof UsernameNotFoundException) {
			message = "用户不存在！";
		} else if (exception instanceof BadCredentialsException) {
			message = "用户名或密码错误！";
		} else if (exception instanceof LockedException) {
			message = "用户已被锁定！";
		} else if (exception instanceof DisabledException) {
			message = "用户不可用！";
		} else if (exception instanceof AccountExpiredException) {
			message = "账户已过期！";
		} else if (exception instanceof CredentialsExpiredException) {
			message = "用户密码已过期！";
		} else {
			message = "登陆认证失败，请联系网站管理员！";
		}
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(JSON.toJSONString(ResultUtil.fail(message)));
	}

}
