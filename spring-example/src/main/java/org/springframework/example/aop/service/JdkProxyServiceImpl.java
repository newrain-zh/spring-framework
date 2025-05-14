package org.springframework.example.aop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JdkProxyServiceImpl implements JdkProxyService {

    @Autowired
    private JdkProxyService jdkProxyService;

    @Override
    public void jdkService() {

    }

    protected void test() {
        System.out.println("jdkTest");
    }


}