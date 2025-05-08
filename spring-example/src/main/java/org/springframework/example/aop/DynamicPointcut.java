package org.springframework.example.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
public class DynamicPointcut extends DynamicMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        // 动态匹配条件（类和方法名）
        return "addUser".equals(method.getName()) && args[0] != null && "admin".equals(args[0].toString());
    }

    @Bean
    public Advisor dynamicAdvisor() {
        return new AbstractPointcutAdvisor() {
            private final Advice advice = (MethodInterceptor) invocation -> {
                System.out.println("【动态切面】拦截方法: " + invocation.getMethod().getName());
                System.out.println("【动态切面】参数: " + Arrays.toString(invocation.getArguments()));
                return invocation.proceed();
            };

            private final        Pointcut pointcut         = new DynamicPointcut();
            private static final long     serialVersionUID = 1L;

            @NotNull
            @Override
            public Pointcut getPointcut() {
                return pointcut;
            }

            @NotNull
            @Override
            public Advice getAdvice() {
                return advice;
            }
        };
    }
}