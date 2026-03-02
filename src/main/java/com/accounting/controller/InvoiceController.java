package com.accounting.controller;

import com.accounting.common.PageResult;
import com.accounting.common.Result;
import com.accounting.dto.request.InvoiceQueryRequest;
import com.accounting.dto.request.InvoiceRequest;
import com.accounting.dto.response.InvoiceResponse;
import com.accounting.entity.Invoice.InvoiceStatus;
import com.accounting.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public Result<PageResult<InvoiceResponse>> query(InvoiceQueryRequest req) {
        return Result.success(invoiceService.query(req));
    }

    @GetMapping("/{id}")
    public Result<InvoiceResponse> get(@PathVariable Long id) {
        return Result.success(invoiceService.getById(id));
    }

    @GetMapping("/overdue")
    public Result<List<InvoiceResponse>> overdue() {
        return Result.success(invoiceService.getOverdue());
    }

    @PostMapping
    public Result<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest req) {
        return Result.success(invoiceService.create(req));
    }

    @PutMapping("/{id}")
    public Result<InvoiceResponse> update(@PathVariable Long id,
                                           @Valid @RequestBody InvoiceRequest req) {
        return Result.success(invoiceService.update(id, req));
    }

    @PatchMapping("/{id}/status")
    public Result<InvoiceResponse> updateStatus(@PathVariable Long id,
                                                 @RequestParam InvoiceStatus status) {
        return Result.success(invoiceService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return Result.success();
    }
}
