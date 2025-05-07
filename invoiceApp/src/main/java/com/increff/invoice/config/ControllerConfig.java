package com.increff.invoice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableSwagger2
public class ControllerConfig implements WebMvcConfigurer {

	@Value("${server.port:9003}")
	private String serverPort;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
				.addResourceLocations("/static/");
		// Add Swagger UI resource handlers
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.increff.invoice"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.host("localhost:" + serverPort)
				.pathMapping("/invoice");
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
			"Invoice API",
			"API for generating and managing invoices",
			"1.0",
			"Terms of service",
			new Contact("Increff", "www.increff.com", "info@increff.com"),
			"License of API",
			"API license URL",
			java.util.Collections.emptyList()
		);
	}
}
