package com.aoping.jiguangpx.config.swagger;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile({ "test", "dev" })
public class Swagger2Configuration {

	/**
	 * 完整的配置参考：{@link "http://springfox.github.io/springfox/docs/current/#springfox-spring-mvc-and-spring-boot"}
	 * 
	 * @return
	 */
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				// 指定controller存放的目录路径
				.apis(RequestHandlerSelectors
				.basePackage("com.aoping.jiguangpx.controller"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.useDefaultResponseMessages(false);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				// 文档标题
				.title("Api Docs")
				// 文档描述
				.description("后台").termsOfServiceUrl("后台").version("v1").build();
	}

}
