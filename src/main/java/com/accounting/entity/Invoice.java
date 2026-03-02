package com.accounting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("invoice")
public class Invoice {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String invoiceNo;
    private String clientName;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal taxRate;
    private String status;  // DRAFT / UNPAID / PARTIAL / PAID
    private String remark;

    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum InvoiceStatus {
        DRAFT, UNPAID, PARTIAL, PAID
    }
}
