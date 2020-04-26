package com.pc.config;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.IOUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = "/invoke/*", description = "拦截所有请求")
@Controller
public class CommonServlet extends HttpServlet implements BeanFactoryAware {

    private Logger LOGGER  = LoggerFactory.getLogger(CommonServlet.class);



    @Autowired
    private BeanFactory beanFactory;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String requestURI = req.getRequestURI().replaceFirst("/invoke/", "");;
        String className = requestURI.substring(0,requestURI.lastIndexOf("."));
        String methodName = requestURI.substring(requestURI.lastIndexOf(".")+1);

        Class<?> aClass;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Failed to find the class {}",className);
            resp.setStatus(503);
            resp.getWriter().write(String.format("Failed to find the class {}",className));
            return;
        }

        Method[] declaredMethods = aClass.getDeclaredMethods();
        List<Method> methodList = new ArrayList<>();
        for (Method method:declaredMethods){
            if (method.getName().equals(methodName)){
                methodList.add(method);
            }
        }

        String requestBody = IOUtils.read(req.getReader());
        List<Object> objects = null;
        Method calledMethod = null;
        for (Method method:methodList){
            try{
                objects = JSONArray.parseArray(requestBody, method.getParameterTypes());
                calledMethod = method;
            }catch (Exception e){
                LOGGER.info("Failed to find the method {}",method.getName());
            }
        }

        if (objects == null){
            LOGGER.info("Failed to find the method {}",methodName);
            resp.setStatus(503);
            resp.getWriter().write(String.format("Failed to find the method {}",methodName));
        }

        // 获取远程调用对象

        System.out.println("OK");
        resp.getWriter().write("pengchi good");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
       this.beanFactory = beanFactory;
    }

}
