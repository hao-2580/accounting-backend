package com.accounting.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceItemDTO {

    @NotBlank(message = "项目描述不能为空")
    private String description;

    @NotNull @DecimalMin(value = "0.01", message = "数量必须大于0")
    private BigDecimal quantity;

    @NotNull @DecimalMin(value = "0", message = "单价不能为负")
    private BigDecimal unitPrice;
}
