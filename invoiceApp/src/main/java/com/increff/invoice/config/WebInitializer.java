package com.increff.invoice.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] {
			DbConfig.class,
			WebConfig.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null; // Using WebConfig for all configuration
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected String getServletName() {
		return "invoiceDispatcher";
	}
}