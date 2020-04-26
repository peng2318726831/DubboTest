package com.pc.api;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String param) {
        return "helloService";
    }
}
