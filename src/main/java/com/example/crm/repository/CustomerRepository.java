package com.example.crm.repository;

import com.example.crm.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 顧客情報リポジトリ
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
