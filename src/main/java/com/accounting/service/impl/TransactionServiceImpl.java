package com.accounting.service.impl;

import com.accounting.common.PageResult;
import com.accounting.dto.request.TransactionQueryRequest;
import com.accounting.dto.request.TransactionRequest;
import com.accounting.dto.response.TransactionResponse;
import com.accounting.entity.Account;
import com.accounting.entity.Transaction;
import com.accounting.exception.ResourceNotFoundException;
import com.accounting.mapper.AccountMapper;
import com.accounting.mapper.TransactionMapper;
import com.accounting.service.TransactionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;

    @Override
    public PageResult<TransactionResponse> query(TransactionQueryRequest req) {
        Page<Transaction> page = new Page<>(req.getPage() + 1, req.getSize());
        
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        if (req.getType() != null) {
            wrapper.eq(Transaction::getType, req.getType());
        }
        if (req.getAccountId() != null) {
            wrapper.eq(Transaction::getAccountId, req.getAccountId());
        }
        if (req.getCategory() != null && !req.getCategory().isEmpty()) {
            wrapper.like(Transaction::getCategory, req.getCategory());
        }
        if (req.getStartDate() != null) {
            wrapper.ge(Transaction::getDate, req.getStartDate());
        }
        if (req.getEndDate() != null) {
            wrapper.le(Transaction::getDate, req.getEndDate());
        }
        wrapper.orderByDesc(Transaction::getDate, Transaction::getId);
        
        Page<Transaction> result = transactionMapper.selectPage(page, wrapper);
        List<TransactionResponse> content = result.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(content, (int) result.getTotal(), (int) result.getCurrent() - 1, (int) result.getSize());
    }

    @Override
    public TransactionResponse getById(Long id) {
        Transaction tx = findById(id);
        return toResponse(tx);
    }

    @Override
    @Transactional
    public TransactionResponse create(TransactionRequest req) {
        Account account = accountMapper.selectById(req.getAccountId());
        if (account == null) {
            throw new ResourceNotFoundException("账户", req.getAccountId());
        }

        Transaction tx = new Transaction();
        tx.setType(req.getType().name());
        tx.setAmount(req.getAmount());
        tx.setCategory(req.getCategory());
        tx.setDate(req.getDate());
        tx.setNote(req.getNote());
        tx.setAccountId(req.getAccountId());
        
        transactionMapper.insert(tx);

        // 更新账户余额
        if (Transaction.TransactionType.INCOME.equals(req.getType())) {
            account.setBalance(account.getBalance().add(req.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(req.getAmount()));
        }
        accountMapper.updateById(account);

        return toResponse(tx);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Transaction tx = findById(id);
        Account account = accountMapper.selectById(tx.getAccountId());
        
        // 回滚账户余额
        if (Transaction.TransactionType.INCOME.name().equals(tx.getType())) {
            account.setBalance(account.getBalance().subtract(tx.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(tx.getAmount()));
        }
        accountMapper.updateById(account);
        
        transactionMapper.deleteById(id);
    }

    @Override
    public List<TransactionResponse> getRecent(int limit) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Transaction::getDate, Transaction::getId)
                .last("LIMIT " + limit);
        List<Transaction> transactions = transactionMapper.selectList(wrapper);
        return transactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Transaction findById(Long id) {
        Transaction tx = transactionMapper.selectById(id);
        if (tx == null) {
            throw new ResourceNotFoundException("交易记录", id);
        }
        return tx;
    }

    private TransactionResponse toResponse(Transaction t) {
        Account account = accountMapper.selectById(t.getAccountId());
        
        TransactionResponse r = new TransactionResponse();
        r.setId(t.getId());
        r.setType(Transaction.TransactionType.valueOf(t.getType()));
        r.setAmount(t.getAmount());
        r.setCategory(t.getCategory());
        r.setDate(t.getDate());
        r.setNote(t.getNote());
        r.setAccountId(t.getAccountId());
        r.setAccountName(account != null ? account.getName() : "");
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}
