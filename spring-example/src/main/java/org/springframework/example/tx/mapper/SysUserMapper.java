package org.springframework.example.tx.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.example.tx.entity.SysUser;

@Mapper
public interface SysUserMapper {

    SysUser getUserById(int id);
}