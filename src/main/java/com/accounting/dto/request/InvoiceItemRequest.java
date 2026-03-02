package com.accounting.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceItemRequest {
    @NotBlank(message = "项目描述不能为空")
    private String description;

    @Min(value = 1, message = "数量最少为1")
    private Integer quantity = 1;

    @DecimalMin(value = "0.00", message = "单价不能为负")
    private BigDecimal unitPrice = BigDecimal.ZERO;
}
