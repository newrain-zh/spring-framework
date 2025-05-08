package org.springframework.example.tx;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.example.tx.entity.SysUser;
import org.springframework.example.tx.service.SysUserService;

public class TxApplicationTest {


    public static void main(String[] args) {
        AnnotationConfigApplicationContext context        = new AnnotationConfigApplicationContext("org.springframework.example.tx");
        SysUserService                     sysUserService = context.getBean(SysUserService.class);
        SysUser                            sysUser        = sysUserService.selectUserById(2);
        System.out.println("查询结果: " + sysUser);
        System.out.println("sysUserService" + sysUserService);
        String[]      beanDefinitionNames = context.getBeanDefinitionNames();
        StringBuilder sb                  = new StringBuilder();
        for (String beanDefinitionName : beanDefinitionNames) {
            sb.append(beanDefinitionName).append(",");
        }
        System.out.println(sb);
        context.close();
    }
}