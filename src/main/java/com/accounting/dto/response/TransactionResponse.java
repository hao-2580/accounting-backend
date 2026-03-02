package com.accounting.dto.response;

import com.accounting.entity.Transaction.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String note;
    private Long accountId;
    private String accountName;
    private LocalDateTime createdAt;
}
