package com.java.logspringmvc.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.java.logspringmvc.dao.LogDAO;
import com.java.logspringmvc.dao.LogDAOImpl;
import com.java.logspringmvc.dao.TokenDAO;
import com.java.logspringmvc.dao.TokenDAOImpl;
import com.java.logspringmvc.dao.UsageMetricDAO;
import com.java.logspringmvc.dao.UsageMetricDAOImpl;
import com.java.logspringmvc.dao.UserDAO;
import com.java.logspringmvc.dao.UserDAOImpl;
import com.java.logspringmvc.util.Decryptlog;

@Configuration
@ComponentScan(basePackages="com.java.logspringmvc")
@PropertySource("classpath:application.properties")
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter {

	@Value("${jdbc.driverClassName}")
	String driver;

	@Value("${jdbc.url}")
	String url;

	@Value("${jdbc.username}")
	String uname;

	@Value("${jdbc.password}")
	String pass;

	@Bean
	public ViewResolver getViewResolver(){
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		return resolver;
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
	@Bean
    public DataSource getDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
	System.out.println("Driver: "+driver);	
	System.out.println("url: "+url);	
	System.out.println("user: "+uname);


        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(uname);
        dataSource.setPassword(pass);
         
        return dataSource;
    }
     
    @Bean
    public LogDAO getLogDAO() {
        return new LogDAOImpl(getDataSource());
    }
    
    @Bean
    public UserDAO getUserDAO() {
        return new UserDAOImpl(getDataSource());
    }
    
    @Bean
    public UsageMetricDAO getUsageMetricDAO() {
        return new UsageMetricDAOImpl(getDataSource());
    }
    
    @Bean
    public Decryptlog getDecryptLog() 
    {
    	return new Decryptlog();
    }
    
    @Bean
    public TokenDAO getTokenDAO() 
    {
    	return new TokenDAOImpl(getDataSource());
    }
//    
//    @Bean(name = "multipartResolver")
//    public MultipartResolver multipartResolver() {
//        return new StandardServletMultipartResolver();
//    }
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(20971520);   // 20MB
        multipartResolver.setMaxInMemorySize(1048576);  // 1MB
        return multipartResolver;
    }
	
}
