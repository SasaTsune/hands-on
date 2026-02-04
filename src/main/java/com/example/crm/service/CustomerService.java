package com.example.crm.service;

import com.example.crm.model.Customer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 顧客情報サービス（インメモリ実装）
 */
@Service
public class CustomerService {
    private final List<Customer> customers = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public CustomerService() {
        // 初期サンプルデータ
        save(new Customer(null, "田中太郎", "tanaka@example.com", "03-1234-5678", "株式会社サンプル"));
        save(new Customer(null, "佐藤花子", "sato@example.com", "03-2345-6789", "テスト株式会社"));
        save(new Customer(null, "鈴木一郎", "suzuki@example.com", "03-3456-7890", "デモ企業"));
    }

    /**
     * 全顧客取得
     */
    public List<Customer> findAll() {
        return new ArrayList<>(customers);
    }

    /**
     * ID指定で顧客取得
     */
    public Optional<Customer> findById(Long id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    /**
     * 顧客保存（新規登録または更新）
     */
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            // 新規登録
            customer.setId(idGenerator.getAndIncrement());
            customers.add(customer);
        } else {
            // 更新
            deleteById(customer.getId());
            customers.add(customer);
        }
        return customer;
    }

    /**
     * 顧客削除
     */
    public void deleteById(Long id) {
        customers.removeIf(c -> c.getId().equals(id));
    }
}
