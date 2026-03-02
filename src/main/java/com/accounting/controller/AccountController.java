package com.accounting.controller;

import com.accounting.common.Result;
import com.accounting.dto.request.AccountRequest;
import com.accounting.dto.response.AccountResponse;
import com.accounting.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public Result<List<AccountResponse>> list() {
        return Result.success(accountService.listAll());
    }

    @GetMapping("/{id}")
    public Result<AccountResponse> get(@PathVariable Long id) {
        return Result.success(accountService.getById(id));
    }

    @PostMapping
    public Result<AccountResponse> create(@Valid @RequestBody AccountRequest req) {
        return Result.success(accountService.create(req));
    }

    @PutMapping("/{id}")
    public Result<AccountResponse> update(@PathVariable Long id,
                                           @Valid @RequestBody AccountRequest req) {
        return Result.success(accountService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return Result.success();
    }
}
