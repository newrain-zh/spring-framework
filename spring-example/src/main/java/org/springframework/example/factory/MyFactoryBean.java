package org.springframework.example.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class MyFactoryBean implements FactoryBean<CustomerFactory> {

    @Override
    public CustomerFactory getObject() throws Exception {
        // 自定义创建逻辑
        return new CustomerFactory("自定义创建的FactoryBean");
    }

    @Override
    public Class<?> getObjectType() {
        return CustomerFactory.class;
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx             = new AnnotationConfigApplicationContext(MyFactoryBean.class);
        MyFactoryBean      myFactoryBean   = ctx.getBean("&myFactoryBean", MyFactoryBean.class);
        CustomerFactory    bean            = ctx.getBean(CustomerFactory.class);
    }
}