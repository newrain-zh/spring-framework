package org.springframework.example.ioc;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.example.aop.service.UserService;

/**
 * BeanFactory演示示例
 */
public class BeanFactoryExample {

    public static void main(String[] args) {
        BeanFactory factory     = new AnnotationConfigApplicationContext(UserService.class);
        UserService userService = factory.getBean(UserService.class);
//        userService.sayHi("BeanFactory");
    }
}