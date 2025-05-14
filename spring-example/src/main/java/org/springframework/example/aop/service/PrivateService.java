package org.springframework.example.aop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivateService {

    @Autowired()
    private TestService testService;

    public void  testPrivate(){

    }
}