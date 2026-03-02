package com.accounting.service;

import com.accounting.common.PageResult;
import com.accounting.dto.request.TransactionQueryRequest;
import com.accounting.dto.request.TransactionRequest;
import com.accounting.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    PageResult<TransactionResponse> query(TransactionQueryRequest req);
    TransactionResponse getById(Long id);
    TransactionResponse create(TransactionRequest request);
    void delete(Long id);
    List<TransactionResponse> getRecent(int limit);
}
