package org.springframework.example.factory;

public class CustomerFactory {

    public CustomerFactory() {
        System.out.println("CustomerFactoryBean#构造方法调用");
    }

    public CustomerFactory(String s) {
        System.out.println(s+"#CustomerFactory");
    }

}