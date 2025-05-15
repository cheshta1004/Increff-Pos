package com.increff.invoice.config;

import com.increff.invoice.InvoiceApplication;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import javax.servlet.Filter;
import java.util.Arrays;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { 
			InvoiceApplication.class,
			DbConfig.class,
			AppConfig.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { 
			WebConfig.class, 
			ControllerConfig.class
		};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected String getServletName() {
		return "invoiceDispatcher";
	}

	@Override
	protected Filter[] getServletFilters() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		corsConfiguration.setAllowedHeaders(Arrays.asList(
			"Authorization",
			"Content-Type",
			"Accept",
			"Origin",
			"X-Requested-With",
			"Access-Control-Request-Method",
			"Access-Control-Request-Headers",
			"X-User-Role"
		));
		corsConfiguration.setExposedHeaders(Arrays.asList(
			"Access-Control-Allow-Origin",
			"Access-Control-Allow-Credentials",
			"Content-Disposition"
		));
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);

		return new Filter[] { new CorsFilter(source) };
	}
}