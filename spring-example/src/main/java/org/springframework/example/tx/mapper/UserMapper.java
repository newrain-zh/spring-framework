package org.springframework.example.tx.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.example.tx.entity.User;

import java.math.BigDecimal;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Update("UPDATE user SET balance = balance + #{amount} WHERE id = #{userId}")
    int deposit(@Param("userId") Long userId, @Param("amount") Double amount);

    @Update("UPDATE user SET balance = balance - #{amount} WHERE id = #{userId}")
    int withdraw(@Param("userId") Long userId, @Param("amount") Double amount);

    @Insert("INSERT INTO user(username, balance) VALUES(#{username}, #{balance})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET balance = balance - #{amount} WHERE username = #{username}")
    int deductBalance(@Param("username") String username, @Param("amount") BigDecimal amount);

    @Update("UPDATE user SET balance = balance + #{amount} WHERE username = #{username}")
    int addBalance(@Param("username") String username, @Param("amount") BigDecimal amount);


}