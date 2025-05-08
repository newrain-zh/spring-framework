package org.springframework.example.aop.introduction;

/**
 * 引入接口具体实现。
 */
public class AuditableImpl implements Auditable {

    @Override
    public void audit() {
        System.out.println("执行审核操作...");
    }
}