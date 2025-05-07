package com.increff.invoice.config;

import java.util.Properties;
import javax.sql.DataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
@PropertySource("classpath:application.properties")
public class DbConfig {

	public static final String[] PACKAGES_TO_SCAN = {
		"com.increff.invoice.pojo",
		"com.increff.invoice.dao",
		"com.increff.invoice.api",
		"com.increff.invoice.dto",
		"com.increff.invoice.service"
	};
	
	@Value("${jdbc.driverClassName}")
	private String jdbcDriver;
	@Value("${jdbc.url}")
	private String jdbcUrl;
	@Value("${jdbc.username}")
	private String jdbcUsername;
	@Value("${jdbc.password}")
	private String jdbcPassword;
	@Value("${hibernate.dialect}")
	private String hibernateDialect;
	@Value("${hibernate.show_sql}")
	private String hibernateShowSql;
	@Value("${hibernate.format_sql}")
	private String hibernateFormatSql;
	@Value("${hibernate.hbm2ddl.auto}")
	private String hibernateHbm2ddl;
	@Value("${hibernate.c3p0.min_size:5}")
	private int c3p0MinSize;
	@Value("${hibernate.c3p0.max_size:20}")
	private int c3p0MaxSize;
	@Value("${hibernate.c3p0.timeout:1800}")
	private int c3p0Timeout;
	@Value("${hibernate.c3p0.max_statements:50}")
	private int c3p0MaxStatements;
	@Value("${hibernate.c3p0.idle_test_period:60}")
	private int c3p0IdleTestPeriod;
	@Value("${hibernate.c3p0.preferredTestQuery:SELECT 1}")
	private String c3p0PreferredTestQuery;
	@Value("${hibernate.c3p0.testConnectionOnCheckin:true}")
	private boolean c3p0TestConnectionOnCheckin;
	@Value("${hibernate.c3p0.testConnectionOnCheckout:true}")
	private boolean c3p0TestConnectionOnCheckout;

	@Bean(name = "dataSource")
	public DataSource getDataSource() throws Exception {
		ComboPooledDataSource bean = new ComboPooledDataSource();
		bean.setDriverClass(jdbcDriver);
		bean.setJdbcUrl(jdbcUrl);
		bean.setUser(jdbcUsername);
		bean.setPassword(jdbcPassword);
		bean.setMinPoolSize(c3p0MinSize);
		bean.setMaxPoolSize(c3p0MaxSize);
		bean.setMaxIdleTime(c3p0Timeout);
		bean.setMaxStatements(c3p0MaxStatements);
		bean.setIdleConnectionTestPeriod(c3p0IdleTestPeriod);
		bean.setPreferredTestQuery(c3p0PreferredTestQuery);
		bean.setTestConnectionOnCheckin(c3p0TestConnectionOnCheckin);
		bean.setTestConnectionOnCheckout(c3p0TestConnectionOnCheckout);
		return bean;
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource);
		bean.setPackagesToScan(PACKAGES_TO_SCAN);
		HibernateJpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter();
		jpaAdapter.setGenerateDdl(true);
		jpaAdapter.setShowSql(true);
		bean.setJpaVendorAdapter(jpaAdapter);
		
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", hibernateDialect);
		jpaProperties.put("hibernate.show_sql", hibernateShowSql);
		jpaProperties.put("hibernate.format_sql", hibernateFormatSql);
		jpaProperties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddl);
		jpaProperties.put("hibernate.c3p0.min_size", String.valueOf(c3p0MinSize));
		jpaProperties.put("hibernate.c3p0.max_size", String.valueOf(c3p0MaxSize));
		jpaProperties.put("hibernate.c3p0.timeout", String.valueOf(c3p0Timeout));
		jpaProperties.put("hibernate.c3p0.max_statements", String.valueOf(c3p0MaxStatements));
		jpaProperties.put("hibernate.c3p0.idle_test_period", String.valueOf(c3p0IdleTestPeriod));
		jpaProperties.put("hibernate.c3p0.preferredTestQuery", c3p0PreferredTestQuery);
		jpaProperties.put("hibernate.c3p0.testConnectionOnCheckin", String.valueOf(c3p0TestConnectionOnCheckin));
		jpaProperties.put("hibernate.c3p0.testConnectionOnCheckout", String.valueOf(c3p0TestConnectionOnCheckout));
		bean.setJpaProperties(jpaProperties);
		return bean;
	}
	
	@Bean(name = "transactionManager")
	public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
		JpaTransactionManager bean = new JpaTransactionManager();
		bean.setEntityManagerFactory(emf.getObject());
		return bean;
	}
}