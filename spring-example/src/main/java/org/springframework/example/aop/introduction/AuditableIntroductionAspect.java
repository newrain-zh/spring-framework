package org.springframework.example.aop.introduction;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;

/**
 * 切面
 * 这里定义了引入的具体切面目标类和默认实现类（扩展逻辑或者说功能增强点）
 */
@Aspect
public class AuditableIntroductionAspect {

    @DeclareParents(value = "org.springframework.example.service.UserServiceImpl", defaultImpl = AuditableImpl.class)
    public Auditable auditable;
}