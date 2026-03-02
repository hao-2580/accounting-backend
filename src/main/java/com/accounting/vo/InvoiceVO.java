package com.accounting.vo;

import com.accounting.entity.Invoice;
import com.accounting.entity.InvoiceItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceVO extends Invoice {
    private String customerName;
    private List<InvoiceItem> items;
}
