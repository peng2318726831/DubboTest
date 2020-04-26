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
        String version = getVersionInfo(interfaceName);
        if (null != version){
            refrenceConfig.setVersion(version);
        }
        refrenceConfig.setRegistry(registryConfig);
        refrenceConfig.setApplication(applicationConfig);
        refrenceConfig.setCheck(false);

        return  refrenceConfig.get();
    }

    /**
     * 获取版本信息，如果没有配置当前类直接所在包版本，则继续向上查看配置
     * @param className
     * @return
     */
    private  String  getVersionInfo(String className) {
        if (StringUtils.isBlank(className)){
            return null;
        }

        String packageName = className;
        int lastIndexOf = className.lastIndexOf(".");
        String version;
        while(lastIndexOf!=-1){
            packageName = packageName.substring(0,lastIndexOf);
            version =  environment.getProperty(packageName);
            if (!StringUtils.isBlank(version)){
                return version;
            }
            lastIndexOf = packageName.lastIndexOf(".");
        }
        return null;
    }

}
