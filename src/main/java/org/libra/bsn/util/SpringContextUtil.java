package org.libra.bsn.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author xianhu.wang
 * @date 2022/10/21 1:22 下午
 * @return null
 */
@Service("springContextUtil")
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (null == SpringContextUtil.applicationContext) {
            SpringContextUtil.applicationContext = applicationContext;
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception e) {

        }
        return null;
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
}
