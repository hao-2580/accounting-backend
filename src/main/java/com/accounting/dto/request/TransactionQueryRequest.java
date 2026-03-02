package com.accounting.dto.request;

import com.accounting.entity.Transaction.TransactionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionQueryRequest {
    private TransactionType type;
    private Long accountId;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private String yearMonth;   // 格式: 2026-02
    private int page = 0;
    private int size = 20;
}
