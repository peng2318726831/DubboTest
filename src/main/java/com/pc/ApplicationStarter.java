package com.pc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ServletComponentScan
@ImportResource({"classpath*:provider.xml"})
public class ApplicationStarter {

    public static  void main(String[] args){
        SpringApplication.run(ApplicationStarter.class,args) ;
    }
}
