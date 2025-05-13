package org.springframework.example.tx.entity;


import lombok.Data;

public class User {
    private Long id;
    private String username;
    private Double balance;

    // 构造方法、getter和setter
    public User() {}

    public User(String username, Double balance) {
        this.username = username;
        this.balance = balance;
    }

    // 省略getter和setter...

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}