package com.aoping.jiguangpx.config.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.alibaba.fastjson.JSON;
import com.aoping.jiguangpx.base.Result;
import com.aoping.jiguangpx.base.ResultCode;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;

	@Autowired
	private UserAccountTicketValidateFilter ticketValidateFilter;

	@Value("${test_or_online}")
	private String testOrOnline;

	// spring security自带的密码加密工具类
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().exceptionHandling().authenticationEntryPoint(new NotLoginAuthenticationEntryPoint()).and()
				.cors().and().authorizeRequests() // 授权配置
				// .antMatchers().permitAll() // 免认证路径
				.antMatchers("/login").permitAll() // 配置免认证路径
				.anyRequest() // 所有请求
				.authenticated() // 都需要认证
				.and().addFilterBefore(ticketValidateFilter, AnonymousAuthenticationFilter.class)
				// // 自定义过滤器
				.formLogin().usernameParameter("username")// 表单方式
				.successHandler(authenticationSuccessHandler) // 处理登录成功
				.failureHandler(authenticationFailureHandler).and().logout().permitAll().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	public void configure(WebSecurity webSecurity) {
		if ("test".equals(testOrOnline)) {
			// swagger文档配置
			webSecurity.ignoring().antMatchers("/swagger-ui.html")// swagger
					.antMatchers("/webjars/**")// api
					.antMatchers("/v2/**")// json
					.antMatchers("/swagger-resources/**");
		}
	}

	public static class NotLoginAuthenticationEntryPoint implements AuthenticationEntryPoint {

		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authException) throws IOException, ServletException {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().write(JSON.toJSONString(new Result<>(ResultCode.NOT_LOGIN)));
			return;
		}

	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("*");// 修改为添加而不是设置，* 最好改为实际的需要，我这是非生产配置，所以粗暴了一点
		configuration.addAllowedMethod("*");// 修改为添加而不是设置
		configuration.addAllowedHeader("*");// 这里很重要，起码需要允许 Access-Control-Allow-Origin
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
