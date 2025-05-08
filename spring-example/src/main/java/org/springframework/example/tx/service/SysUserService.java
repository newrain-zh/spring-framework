package org.springframework.example.tx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.example.tx.entity.SysUser;
import org.springframework.example.tx.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

//    @Transactional
    public SysUser selectUserById(Integer id) {
        return sysUserMapper.getUserById(id);
    }
}