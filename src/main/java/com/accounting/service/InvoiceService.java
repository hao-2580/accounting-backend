package com.accounting.service;

import com.accounting.common.PageResult;
import com.accounting.dto.request.InvoiceQueryRequest;
import com.accounting.dto.request.InvoiceRequest;
import com.accounting.dto.response.InvoiceResponse;
import com.accounting.entity.Invoice.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    PageResult<InvoiceResponse> query(InvoiceQueryRequest req);
    InvoiceResponse getById(Long id);
    InvoiceResponse create(InvoiceRequest request);
    InvoiceResponse update(Long id, InvoiceRequest request);
    InvoiceResponse updateStatus(Long id, InvoiceStatus status);
    void delete(Long id);
    List<InvoiceResponse> getOverdue();
}
