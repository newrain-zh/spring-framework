package org.springframework.example.service;

public interface UserService {

    void addUser(String username);

    void deleteUser(String username) throws Exception;

    String getUser(String username);


}