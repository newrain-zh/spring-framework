package org.springframework.example.tx;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.example.tx.service.UserService;


public class TxApplicationTest {


    public static void main(String[] args) {
        AnnotationConfigApplicationContext context     = new AnnotationConfigApplicationContext("org.springframework.example.tx");

        testSingleTx(context);
        String[]      beanDefinitionNames = context.getBeanDefinitionNames();
        StringBuilder sb                  = new StringBuilder();
        for (String beanDefinitionName : beanDefinitionNames) {
            sb.append(beanDefinitionName).append(",").append("\n");
        }
        System.out.println("注册的 Bean");
        System.out.println(sb);
        context.close();
    }


    public static void testSingleTx(AnnotationConfigApplicationContext context) {
        UserService                        userService = context.getBean(UserService.class);
        userService.transfer(1L, 2L, 100.0);
    }

    public static void testMultiTx(AnnotationConfigApplicationContext context) {


    }

}