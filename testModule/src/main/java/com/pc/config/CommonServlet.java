package com.pc.config;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pc.beanfactory.DubboBeanFacotory;
import com.pc.domain.ErrorResult;
import com.pc.domain.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//@WebServlet(urlPatterns = "/invoke/*", description = "拦截所有请求")
@Component
public class CommonServlet extends HttpServlet{

    private Logger LOGGER  = LoggerFactory.getLogger(CommonServlet.class);

    @Autowired
    private DubboBeanFacotory beanFactory;

//    @Override
//    public void init() throws ServletException {
//        super.init();
//    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        ErrorResult errorResult = null;
        if (!req.getMethod().equals("POST")){
            errorResult = new ErrorResult(ResultCode.SERVICE_FAIL.getResultCode(), "only support the post request method");
            sendData(resp,errorResult);
            return;
        }

        String requestURI = req.getPathInfo().substring(1);
        String className = requestURI.substring(0,requestURI.lastIndexOf("."));
        String methodName = requestURI.substring(requestURI.lastIndexOf(".")+1);

        Class<?> aClass;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Failed to find the class {}",className);
            errorResult = new ErrorResult(ResultCode.SERVICE_FAIL.getResultCode(),
                    String.format("Failed to find the class {}",className));
            sendData(resp,errorResult);
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
            }
        }

        if (objects == null || calledMethod==null){
            LOGGER.error("Failed to find the method {}",methodName);
            errorResult = new ErrorResult(ResultCode.SERVICE_FAIL.getResultCode(), String.format("Failed to find the method {}",methodName));
            sendData(resp,errorResult);
            return;
        }

        // 获取远程调用对象
        Object bean = beanFactory.getBean(className);

        Object result = null;
        try {
             result = calledMethod.invoke(bean,objects.toArray());
        } catch (InvocationTargetException e){
            LOGGER.error("call the service exception "+requestURI,e);
            errorResult = new ErrorResult(ResultCode.SERVICE_FAIL.getResultCode(), e.getTargetException().getMessage());
            sendData(resp,errorResult);
            return;
        }catch (Exception e) {
            LOGGER.error("call the service exception "+requestURI,e);
            errorResult = new ErrorResult(ResultCode.SERVICE_FAIL.getResultCode(), e.getMessage());
            sendData(resp,errorResult);
            return;
        }
        sendData(resp,result);
        return;
    }




    private void sendData( HttpServletResponse resp,Object obj) throws IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONObject.toJSONString(obj));
        writer.flush();
    }

    

}
