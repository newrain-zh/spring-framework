package org.springframework.example.aop.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.example.aop.introduction.Auditable;

import java.util.Arrays;

public class AopContextTest {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext("org.springframework.example.aop");
//        testAop(ctx);
        testPrivate(ctx);
        System.out.println("已加载的 Bean name:" + Arrays.toString(ctx.getBeanDefinitionNames()));

    }

    public static void testAop(ApplicationContext ctx) {
        UserService userService = ctx.getBean(UserService.class); // 通过接口获取实现类
        userService.addUser("admin");
        System.out.println(userService.getClass().getName());
        // 转换为 Auditable调用
        if (userService instanceof Auditable) {
            ((Auditable) userService).audit();
        }
    }

    public static void testPrivate(ApplicationContext ctx) {
        TestService testService = ctx.getBean(TestService.class);
        testService.testProtected();
    }


}