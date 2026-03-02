package com.accounting.dto.request;

import com.accounting.entity.Invoice.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceRequest {
    @NotBlank(message = "客户名称不能为空")
    private String clientName;

    @NotNull(message = "开票日期不能为空")
    private LocalDate issueDate;

    @NotNull(message = "到期日期不能为空")
    private LocalDate dueDate;

    @DecimalMin("0") @DecimalMax("100")
    private BigDecimal taxRate = BigDecimal.valueOf(13);

    private InvoiceStatus status = InvoiceStatus.DRAFT;
    private String remark;

    @NotEmpty(message = "发票明细不能为空")
    @Valid
    private List<InvoiceItemRequest> items;
}
