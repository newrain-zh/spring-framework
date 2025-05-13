package org.springframework.example.tx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.example.tx.entity.TransferLog;
import org.springframework.example.tx.mapper.TransferLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private TransferLogMapper transferLogMapper;

    // 独立事务：使用 REQUIRES_NEW
    @Transactional( propagation = Propagation.REQUIRES_NEW)
//    @Transactional()
    public void recordLog(TransferLog log) {
        transferLogMapper.insertLog(log);
        // 测试：即使主事务回滚，日志仍然提交
        // throw new RuntimeException("日志记录失败");
    }
}