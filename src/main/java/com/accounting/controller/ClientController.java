package com.accounting.controller;

import com.accounting.dto.ClientDTO;
import com.accounting.dto.Result;
import com.accounting.entity.Client;
import com.accounting.service.ClientService;
import com.accounting.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @Operation(summary = "客户列表")
    @GetMapping
    public Result<List<Client>> list() {
        return Result.ok(clientService.listByUser(SecurityUtil.getCurrentUserId()));
    }

    @Operation(summary = "新增客户")
    @PostMapping
    public Result<Client> create(@Valid @RequestBody ClientDTO dto) {
        return Result.ok(clientService.create(SecurityUtil.getCurrentUserId(), dto));
    }

    @Operation(summary = "更新客户")
    @PutMapping("/{id}")
    public Result<Client> update(@PathVariable Long id, @Valid @RequestBody ClientDTO dto) {
        return Result.ok(clientService.update(SecurityUtil.getCurrentUserId(), id, dto));
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        clientService.delete(SecurityUtil.getCurrentUserId(), id);
        return Result.ok();
    }
}
