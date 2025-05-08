package org.springframework.example.bean;

import org.springframework.stereotype.Component;

@Component
public class MyBean {

    public MyBean() {
        System.out.println("MyBean 构造方法调用");
    }

    public MyBean(String s) {
        System.out.println(s);
    }
}