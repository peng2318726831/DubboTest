package com.pc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Autowired
    private CommonServlet commonServlet;

    @Bean
    public ServletRegistrationBean servletBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.addUrlMappings("/invoke/*");
        registrationBean.setServlet(commonServlet);
        return registrationBean;
    }
}
