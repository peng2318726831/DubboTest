package com.pc.config;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pc.beanfactory.DubboBeanFacotory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@WebServlet(urlPatterns = "/invoke/*", description = "拦截所有请求")
@Controller
public class CommonServlet extends HttpServlet{

    private Logger LOGGER  = LoggerFactory.getLogger(CommonServlet.class);

    @Autowired
    private DubboBeanFacotory beanFactory;

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
        Object bean = beanFactory.getBean(className);

        try {
            Object result = calledMethod.invoke(bean,objects.toArray());
            System.out.println(JSONObject.toJSONString(result));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        System.out.println("OK");
        resp.getWriter().write("pengchi good");
    }

    

}
