package com.accounting.service;

import com.accounting.dto.request.AccountRequest;
import com.accounting.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {
    List<AccountResponse> listAll();
    AccountResponse getById(Long id);
    AccountResponse create(AccountRequest request);
    AccountResponse update(Long id, AccountRequest request);
    void delete(Long id);
}
