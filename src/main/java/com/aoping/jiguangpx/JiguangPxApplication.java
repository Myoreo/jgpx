package com.aoping.jiguangpx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aoping.jiguangpx.dao")
public class JiguangPxApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(JiguangPxApplication.class, args);
	}
}
