package com.increff.invoice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean(name = "restTemplate")
    @Primary
    public RestTemplate restTemplate() {
        logger.info("Creating RestTemplate bean in AppConfig");
        try {
            RestTemplate restTemplate = new RestTemplate();
            logger.info("RestTemplate bean created successfully in AppConfig");
            return restTemplate;
        } catch (Exception e) {
            logger.error("Error creating RestTemplate bean in AppConfig", e);
            throw e;
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
} 