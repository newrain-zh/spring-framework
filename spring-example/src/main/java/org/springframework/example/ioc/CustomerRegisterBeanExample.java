package org.springframework.example.ioc;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.example.aop.service.UserService;
import org.springframework.example.aop.service.UserServiceImpl;

/**
 * 手动注册 Bean 并获取 Bean实例
 */
public class CustomerRegisterBeanExample {


    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 1. 注册 Bean 定义
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(UserServiceImpl.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);
        // 2. 添加后置处理器（可选）
        beanFactory.addBeanPostProcessor(new MyBeanPostProcessor());
        // 3. 获取 Bean 实例
        UserService userService = beanFactory.getBean("userService", UserService.class);
        userService.addUser("newrain-zh");
    }

    static class MyBeanPostProcessor implements BeanPostProcessor {

    }
}