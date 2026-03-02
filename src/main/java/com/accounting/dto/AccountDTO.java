package com.accounting.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountDTO {
    @NotBlank(message = "账户名称不能为空")
    private String name;

    @NotBlank(message = "账户类型不能为空")
    private String type;

    @DecimalMin(value = "0", message = "初始余额不能为负")
    private BigDecimal balance = BigDecimal.ZERO;

    private String currency = "CNY";
    private String description;
}
