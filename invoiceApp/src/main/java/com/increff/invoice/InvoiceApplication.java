package com.increff.invoice;

import com.increff.invoice.config.AppConfig;
import com.increff.invoice.config.ControllerConfig;
import com.increff.invoice.config.DbConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {
    "com.increff.invoice.controller",
    "com.increff.invoice.dto",
    "com.increff.invoice.service",
    "com.increff.invoice.api",
    "com.increff.invoice.dao",
    "com.increff.invoice.config"
})
@Import({AppConfig.class, ControllerConfig.class, DbConfig.class})
@EnableTransactionManagement
public class InvoiceApplication {
    // Configuration class
} 