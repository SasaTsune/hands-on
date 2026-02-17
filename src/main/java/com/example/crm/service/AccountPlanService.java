package com.example.crm.service;

import com.example.crm.model.AccountPlan;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * アカウントプランサービス（インメモリ実装）
 */
@Service
public class AccountPlanService {
    private final List<AccountPlan> accountPlans = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 顧客IDに紐づくアカウントプランを取得
     */
    public List<AccountPlan> findByCustomerId(Long customerId) {
        return accountPlans.stream()
                .filter(ap -> ap.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * ID指定でアカウントプラン取得
     */
    public Optional<AccountPlan> findById(Long id) {
        return accountPlans.stream()
                .filter(ap -> ap.getId().equals(id))
                .findFirst();
    }

    /**
     * アカウントプラン登録
     */
    public AccountPlan save(AccountPlan accountPlan) {
        LocalDateTime now = LocalDateTime.now();
        if (accountPlan.getId() == null) {
            accountPlan.setId(idGenerator.getAndIncrement());
            accountPlan.setCreatedAt(now);
            accountPlan.setUpdatedAt(now);
            accountPlans.add(accountPlan);
        } else {
            Optional<AccountPlan> existing = findById(accountPlan.getId());
            existing.ifPresent(e -> accountPlan.setCreatedAt(e.getCreatedAt()));
            deleteById(accountPlan.getId());
            accountPlan.setUpdatedAt(now);
            accountPlans.add(accountPlan);
        }
        return accountPlan;
    }

    /**
     * アカウントプラン削除
     */
    public void deleteById(Long id) {
        accountPlans.removeIf(ap -> ap.getId().equals(id));
    }
}
