package com.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.accounting.dto.CustomerDTO;
import com.accounting.entity.Customer;
import com.accounting.exception.BusinessException;
import com.accounting.mapper.CustomerMapper;
import com.accounting.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    @Override
    public List<Customer> listAll() {
        return customerMapper.selectList(
                new LambdaQueryWrapper<Customer>().orderByDesc(Customer::getCreatedAt)
        );
    }

    @Override
    public Customer getById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) throw new BusinessException("客户不存在");
        return customer;
    }

    @Override
    public Customer create(CustomerDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        customerMapper.insert(customer);
        return customer;
    }

    @Override
    public Customer update(Long id, CustomerDTO dto) {
        Customer customer = getById(id);
        BeanUtils.copyProperties(dto, customer);
        customerMapper.updateById(customer);
        return customer;
    }

    @Override
    public void delete(Long id) {
        getById(id);
        customerMapper.deleteById(id);
    }
}
