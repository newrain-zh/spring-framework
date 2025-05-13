package org.springframework.example.tx.service;

import org.springframework.example.tx.entity.User;

public interface UserService {

    void transfer(Long fromUserId, Long toUserId, Double amount);

    User createUser(String username, Double balance);

    User getUserById(Long id);
}