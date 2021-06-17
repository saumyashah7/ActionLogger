package com.java.logspringmvc.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MyWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	    @Override
	    protected Class<?>[] getRootConfigClasses() {
	        return null;
	    }

	    @Override
	    protected Class<?>[] getServletConfigClasses() {
	        return new Class[]{MvcConfiguration.class};
	    }

	    @Override
	    protected String[] getServletMappings() {
	        return new String[]{"/"};
	    }

}
