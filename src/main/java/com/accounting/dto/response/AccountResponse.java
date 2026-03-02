package com.accounting.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private String name;
    private String type;
    private BigDecimal balance;
    private String remark;
    private LocalDateTime createdAt;
}
