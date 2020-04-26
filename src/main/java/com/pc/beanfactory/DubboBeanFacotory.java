package com.pc.beanfactory;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class DubboBeanFacotory {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private RegistryConfig registryConfig;

    @Autowired
    private Environment environment;

    private ConcurrentHashMap<String,Object> cachedBeanMap = new ConcurrentHashMap<>();

    public  <T> T getBean(String interfaceName){
        if (null == cachedBeanMap.get(interfaceName)){
            synchronized (this){
                if (null == cachedBeanMap.get(interfaceName)){
                    T bean = (T)createBean(interfaceName);
                    cachedBeanMap.put(interfaceName,bean);
                    return bean;
                }
            }
        }
        return (T)cachedBeanMap.get(interfaceName);
    }

    private Object createBean(String interfaceName){
        ReferenceConfig<Object> refrenceConfig = new ReferenceConfig<>();
        refrenceConfig.setInterface(interfaceName);
        refrenceConfig.setVersion(getVersionInfo(interfaceName));
        refrenceConfig.setRegistry(registryConfig);
        refrenceConfig.setApplication(applicationConfig);

        return  refrenceConfig.get();
    }

    /**
     * 获取版本信息，如果没有配置当前类直接所在包版本，则继续向上查看配置
     * @param className
     * @return
     */
    private  String  getVersionInfo(String className){
        if (StringUtils.isBlank(className)){
            return null;
        }

        int lastIndexOf = className.length()-1;
        String version;
        while(lastIndexOf!=-1){
            version =  environment.getProperty(className);
            if (!StringUtils.isBlank(version)){
                return version;
            }
            lastIndexOf = className.lastIndexOf(".");
            className.substring(0,lastIndexOf);
        }
        return null;
    }

}
