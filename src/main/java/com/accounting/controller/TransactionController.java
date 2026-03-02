package com.accounting.controller;

import com.accounting.common.PageResult;
import com.accounting.common.Result;
import com.accounting.dto.request.TransactionQueryRequest;
import com.accounting.dto.request.TransactionRequest;
import com.accounting.dto.response.TransactionResponse;
import com.accounting.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Result<PageResult<TransactionResponse>> query(TransactionQueryRequest req) {
        return Result.success(transactionService.query(req));
    }

    @GetMapping("/{id}")
    public Result<TransactionResponse> get(@PathVariable Long id) {
        return Result.success(transactionService.getById(id));
    }

    @PostMapping
    public Result<TransactionResponse> create(@Valid @RequestBody TransactionRequest req) {
        return Result.success(transactionService.create(req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return Result.success();
    }
}
