package com.accounting.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceCreateDTO {

    @NotNull(message = "客户不能为空")
    private Long customerId;

    @NotNull(message = "开票日期不能为空")
    private LocalDate date;

    @NotNull(message = "到期日不能为空")
    private LocalDate dueDate;

    @DecimalMin(value = "0", message = "税率不能为负")
    @DecimalMax(value = "100", message = "税率不能超过100")
    private BigDecimal taxRate = new BigDecimal("13");

    private String note;

    @Valid
    @NotEmpty(message = "发票明细不能为空")
    private List<InvoiceItemDTO> items;
}
