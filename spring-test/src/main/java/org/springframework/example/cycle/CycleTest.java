package org.springframework.example.cycle;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class CycleTest {

    public static void main(String[] args) {
        ApplicationContext ctx                 = new AnnotationConfigApplicationContext(A.class, B.class);
        A                  beanA               = ctx.getBean(A.class);
        B                  beanB               = ctx.getBean(B.class);
        String[]           beanDefinitionNames = ctx.getBeanDefinitionNames();
        System.out.println("====================");
        System.out.println(Arrays.toString(beanDefinitionNames));
    }
}