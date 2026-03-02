package com.accounting.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDTO {
    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "INCOME|EXPENSE", message = "类型必须为 INCOME 或 EXPENSE")
    private String type;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotNull(message = "账户不能为空")
    private Long accountId;

    @NotNull(message = "交易日期不能为空")
    private LocalDate transactionDate;

    private String note;
}
