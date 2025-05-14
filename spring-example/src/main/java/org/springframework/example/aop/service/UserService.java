package org.springframework.example.aop.service;

public interface UserService {

    void addUser(String username);

    void deleteUser(String username) throws Exception;

    String getUser(String username);


}