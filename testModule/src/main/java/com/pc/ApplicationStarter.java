package com.pc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ServletComponentScan
@ImportResource({"classpath*:provider.xml"})
public class ApplicationStarter extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApplicationStarter.class);
    }

    public static  void main(String[] args){
        SpringApplication.run(ApplicationStarter.class,args) ;
    }
}
