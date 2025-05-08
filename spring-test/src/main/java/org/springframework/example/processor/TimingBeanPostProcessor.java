package org.springframework.example.processor;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统计 Bean 初始化耗时
 */
//@Component // 一定要加这个注解 不然 Spring 无法识别
public class TimingBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Long> startTimes = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        startTimes.put(beanName, System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Long          startTime     = startTimes.remove(beanName);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println(beanName + "Bean初始化耗时" + duration + "ms");
        }
        return bean;
    }
}