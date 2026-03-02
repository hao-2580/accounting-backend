package com.accounting.dto.request;

import com.accounting.entity.Transaction.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    @NotNull(message = "类型不能为空")
    private TransactionType type;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "分类不能为空")
    private String category;

    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @NotNull(message = "账户不能为空")
    private Long accountId;

    private String note;
}
