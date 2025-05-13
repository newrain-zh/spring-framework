package org.springframework.example.tx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.example.tx.entity.TransferLog;
import org.springframework.example.tx.entity.User;
import org.springframework.example.tx.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogService logService;


    @Override
    @Transactional ()
    public void transfer(Long fromUserId, Long toUserId, Double amount) {
        // 检查用户是否存在
        User fromUser = userMapper.findById(fromUserId);
        User toUser   = userMapper.findById(toUserId);

        if (fromUser == null || toUser == null) {
            throw new RuntimeException("用户不存在");
        }
        // 检查余额是否足够
        if (fromUser.getBalance() < amount) {
            throw new RuntimeException("余额不足");
        }
        // 扣款
        userMapper.withdraw(fromUserId, amount);
        // 存款
        userMapper.deposit(toUserId, amount);
        TransferLog log = new TransferLog();
        log.setTransactionId(UUID.randomUUID().toString());
        log.setFromUsername(fromUser.getUsername());
        log.setToUsername(toUser.getUsername());
        log.setRemark("测试事务1");
        log.setAmount(new BigDecimal(amount + ""));
        log.setStatus("SUCCESS");
        // logS
        logService.recordLog(log);
        // 模拟异常，测试事务回滚
        if (true) throw new RuntimeException("模拟转账异常");
    }

    @Override
    @Transactional
    public User createUser(String username, Double balance) {
        User user = new User(username, balance);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

}