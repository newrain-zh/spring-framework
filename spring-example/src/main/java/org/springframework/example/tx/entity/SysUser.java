
package org.springframework.example.tx.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUser {

    private Long id;

    private Long officeId;

    private String name;

    private String account;

    private String password;

    private LocalDateTime lastLoginTime;

    private Integer loginCount = 0;

    private Boolean isEnable;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}