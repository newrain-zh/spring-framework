package org.springframework.example.aop.service;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String username) {
        System.out.println("添加用户：" + username);
    }

    @Override
    public void deleteUser(String username) throws Exception {

    }

    @Override
    public String getUser(String username) {
        return "";
    }


    private void test() {
        System.out.println("test");
    }
}