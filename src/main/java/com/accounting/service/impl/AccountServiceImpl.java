package com.accounting.service.impl;

import com.accounting.dto.request.AccountRequest;
import com.accounting.dto.response.AccountResponse;
import com.accounting.entity.Account;
import com.accounting.exception.BusinessException;
import com.accounting.exception.ResourceNotFoundException;
import com.accounting.mapper.AccountMapper;
import com.accounting.service.AccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;

    @Override
    public List<AccountResponse> listAll() {
        return accountMapper.selectList(null).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional
    public AccountResponse create(AccountRequest req) {
        Account existing = accountMapper.selectOne(
                new LambdaQueryWrapper<Account>().eq(Account::getName, req.getName()));
        if (existing != null) {
            throw new BusinessException("账户名称已存在：" + req.getName());
        }
        Account account = new Account();
        account.setName(req.getName());
        account.setType(req.getType());
        account.setBalance(req.getBalance() != null ? req.getBalance() : BigDecimal.ZERO);
        account.setRemark(req.getRemark());
        accountMapper.insert(account);
        return toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse update(Long id, AccountRequest req) {
        Account account = findById(id);
        account.setName(req.getName());
        account.setType(req.getType());
        account.setRemark(req.getRemark());
        accountMapper.updateById(account);
        return toResponse(account);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        accountMapper.deleteById(id);
    }

    private Account findById(Long id) {
        Account account = accountMapper.selectById(id);
        if (account == null) {
            throw new ResourceNotFoundException("账户", id);
        }
        return account;
    }

    private AccountResponse toResponse(Account a) {
        AccountResponse r = new AccountResponse();
        r.setId(a.getId());
        r.setName(a.getName());
        r.setType(a.getType());
        r.setBalance(a.getBalance());
        r.setRemark(a.getRemark());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}
