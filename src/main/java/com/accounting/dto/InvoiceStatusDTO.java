package com.accounting.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceStatusDTO {
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "DRAFT|UNPAID|PARTIAL|PAID", message = "状态值非法")
    private String status;

    @DecimalMin("0")
    private BigDecimal paidAmount;
}
