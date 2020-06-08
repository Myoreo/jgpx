package com.aoping.jiguangpx.config;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	/**
	 * 注册拦截器
	 * 
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		Map<String, AutoRegisteredInterceptor> interceptors = this.applicationContext
				.getBeansOfType(AutoRegisteredInterceptor.class);
		Collection<AutoRegisteredInterceptor> values = interceptors.values();
		TreeMap<Integer, AutoRegisteredInterceptor> treeMap = new TreeMap<>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});
		for (AutoRegisteredInterceptor autoRegisteredInterceptor : values) {
			int value = autoRegisteredInterceptor.getClass().getAnnotation(Order.class).value();
			treeMap.put(value, autoRegisteredInterceptor);
			log.info("拦截器 v    " + value);
		}

		for (AutoRegisteredInterceptor interceptor : treeMap.values()) {
			if (interceptor instanceof HandlerInterceptor) {
				registry.addInterceptor((HandlerInterceptor) interceptor);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
