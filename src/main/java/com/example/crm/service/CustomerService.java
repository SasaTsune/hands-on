package com.example.crm.service;

import com.example.crm.model.Customer;
import com.example.crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 顧客情報サービス
 */
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * 全顧客取得
     */
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /**
     * ID指定で顧客取得
     */
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * 顧客保存（新規登録または更新）
     */
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * 顧客削除
     */
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }
}
