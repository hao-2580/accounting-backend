package com.accounting.dto.response;

import com.accounting.entity.Invoice.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNo;
    private String clientName;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal taxRate;
    private InvoiceStatus status;
    private String remark;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private boolean overdue;
    private List<InvoiceItemResponse> items;
    private LocalDateTime createdAt;
}
