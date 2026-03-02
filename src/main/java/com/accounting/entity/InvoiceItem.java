package com.accounting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("invoice_item")
public class InvoiceItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long invoiceId;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;

    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
