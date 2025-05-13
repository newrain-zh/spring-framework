package org.springframework.example.tx;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

//@Aspect
//@Component //必须要加此注解
//@EnableAspectJAutoProxy // 使 Aspect注解生效
//@EnableAspectJAutoProxy(proxyTargetClass = true) // 默认为 jdk动态代理 [proxyTargetClass = true cglib动态代理]
public class LoggingAspect {

    //    @Pointcut("execution(* org.springframework.example.service.*(..))")
//    @Pointcut("execution(* org.springframework.example.tx.service.*.*(..))")
    public void serviceMethods() {
    }

//    @Before("serviceMethods()")
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println("[注解前置通知] 方法名称: " + joinPoint.getSignature().getName());
    }

//    @After("serviceMethods()")
    public void afterAdvice() {
        System.out.println("[注解后置通知] 方法执行结束");
    }

}