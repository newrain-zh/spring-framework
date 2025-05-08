package org.springframework.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.example.service.UserService;
import org.springframework.example.processor.TimingBeanPostProcessor;

import java.util.Arrays;

public class ContextTest {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(UserService.class, TimingBeanPostProcessor.class);
//        ApplicationContext ctx = new AnnotationConfigApplicationContext(UserService.class);
//        ApplicationContext ctx    = new ConfigurableApplicationContext(AppConfig.class);
        // 获取普通 Bean
        UserService userService         = ctx.getBean(UserService.class);

        // 显式初始化 MyFactoryBean
        // 获取 FactoryBean 本身
//        MyFactoryBean myFactoryBean = ctx.getBean("&myFactoryBean", MyFactoryBean.class);
        // 获取 FactoryBean 的实例
//        CustomerFactory customerFactory = (CustomerFactory) ctx.getBean("myFactoryBean");
        String[]    beanDefinitionNames = ctx.getBeanDefinitionNames();
        System.out.println("====================");
        System.out.println(Arrays.toString(beanDefinitionNames));
    }
}