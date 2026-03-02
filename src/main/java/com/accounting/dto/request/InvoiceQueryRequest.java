package com.accounting.dto.request;

import com.accounting.entity.Invoice.InvoiceStatus;
import lombok.Data;

@Data
public class InvoiceQueryRequest {
    private InvoiceStatus status;
    private String query;  // 搜索客户名/发票号
    private int page = 0;
    private int size = 20;
}
