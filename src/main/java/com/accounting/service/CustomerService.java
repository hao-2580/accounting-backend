package com.accounting.service;

import com.accounting.dto.CustomerDTO;
import com.accounting.entity.Customer;
import java.util.List;

public interface CustomerService {
    List<Customer> listAll();
    Customer getById(Long id);
    Customer create(CustomerDTO dto);
    Customer update(Long id, CustomerDTO dto);
    void delete(Long id);
}
