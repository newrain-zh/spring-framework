package org.springframework.example.aop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("testService")
public class TestService {

    @Autowired()
    private TestService testService;

    @Autowired
    private JdkProxyService jdkProxyService;

    private void privateMethod() {
        System.out.println("私有方法");
    }

    public void testPublic() {
        System.out.println("共有方法");
        testService.privateMethod();
        testService.testProtected();
    }

    protected void testProtected() {
        System.out.println("protected");
    }
    public void testJdk(){
        jdkProxyService.jdkService();
    }


}