package com.accounting.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceDTO {
    @NotNull(message = "客户ID不能为空")
    private Long clientId;

    @NotNull(message = "开票日期不能为空")
    private LocalDate issueDate;

    @NotNull(message = "到期日期不能为空")
    private LocalDate dueDate;

    @DecimalMin("0") @DecimalMax("100")
    private BigDecimal taxRate = new BigDecimal("13");

    private String note;

    @NotEmpty(message = "发票明细不能为空")
    private List<InvoiceItemDTO> items;

    @Data
    public static class InvoiceItemDTO {
        @NotBlank(message = "项目描述不能为空")
        private String description;

        @DecimalMin("0.01")
        private BigDecimal quantity = BigDecimal.ONE;

        @DecimalMin("0.01")
        private BigDecimal unitPrice;

        private Integer sortOrder = 0;
    }
}
