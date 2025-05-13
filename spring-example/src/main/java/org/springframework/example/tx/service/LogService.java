package org.springframework.example.tx.service;

import org.springframework.example.tx.entity.TransferLog;

public interface LogService {

    void recordLog(TransferLog log);
}