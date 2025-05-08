package org.springframework.example.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.example.aop.introduction.AuditableIntroductionAspect;
import org.springframework.example.factory.MyFactoryBean;
import org.springframework.example.service.UserService;
import org.springframework.example.service.UserServiceImpl;

@Configuration
@ComponentScan(basePackages = "org.springframework.example.service") // 替换为你的包路径
public class AppConfig {


//    @Bean
    public MyFactoryBean myFactoryBean() {
        return new MyFactoryBean();
    }


    @Bean
    public AuditableIntroductionAspect auditableAspect() {
        return new AuditableIntroductionAspect();
    }
}