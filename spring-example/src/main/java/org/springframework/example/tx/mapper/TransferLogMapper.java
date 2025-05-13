package org.springframework.example.tx.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.example.tx.entity.TransferLog;

@Mapper
public interface TransferLogMapper {
    @Insert("INSERT INTO transfer_log(transaction_id, from_username, to_username, amount, status) " +
            "VALUES(#{transactionId}, #{fromUsername}, #{toUsername}, #{amount}, #{status})")
    int insertLog(TransferLog log);
}