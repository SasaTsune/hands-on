package com.example.crm.controller;

import com.example.crm.model.AccountPlan;
import com.example.crm.model.Customer;
import com.example.crm.service.AccountPlanService;
import com.example.crm.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * アカウントプラン管理コントローラー
 */
@Controller
@RequestMapping("/customers/{customerId}/account-plan")
public class AccountPlanController {

    private final AccountPlanService accountPlanService;
    private final CustomerService customerService;

    public AccountPlanController(AccountPlanService accountPlanService, CustomerService customerService) {
        this.accountPlanService = accountPlanService;
        this.customerService = customerService;
    }

    /**
     * アカウントプラン表示
     */
    @GetMapping
    public String show(@PathVariable Long customerId, Model model) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("顧客が見つかりません: " + customerId));
        List<AccountPlan> plans = accountPlanService.findByCustomerId(customerId);
        AccountPlan accountPlan = plans.isEmpty() ? null : plans.get(0);
        model.addAttribute("customer", customer);
        model.addAttribute("accountPlan", accountPlan);
        return "account-plan/show";
    }

    /**
     * アカウントプラン登録フォーム表示
     */
    @GetMapping("/new")
    public String newForm(@PathVariable Long customerId, Model model) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("顧客が見つかりません: " + customerId));
        AccountPlan accountPlan = new AccountPlan();
        accountPlan.setCustomerId(customerId);
        model.addAttribute("customer", customer);
        model.addAttribute("accountPlan", accountPlan);
        model.addAttribute("isEdit", false);
        return "account-plan/form";
    }

    /**
     * アカウントプラン保存
     */
    @PostMapping("/save")
    public String save(@PathVariable Long customerId, @ModelAttribute AccountPlan accountPlan) {
        customerService.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("顧客が見つかりません: " + customerId));

        if (accountPlan.getId() == null) {
            List<AccountPlan> existingPlans = accountPlanService.findByCustomerId(customerId);
            if (!existingPlans.isEmpty()) {
                accountPlan.setId(existingPlans.get(0).getId());
            }
        }

        accountPlan.setCustomerId(customerId);
        accountPlanService.save(accountPlan);
        return "redirect:/customers/" + customerId + "/account-plan";
    }

    /**
     * アカウントプラン編集フォーム表示
     */
    @GetMapping("/edit")
    public String editForm(@PathVariable Long customerId, Model model) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("顧客が見つかりません: " + customerId));
        List<AccountPlan> plans = accountPlanService.findByCustomerId(customerId);
        if (plans.isEmpty()) {
            return "redirect:/customers/" + customerId + "/account-plan/new";
        }
        AccountPlan accountPlan = plans.get(0);
        model.addAttribute("customer", customer);
        model.addAttribute("accountPlan", accountPlan);
        model.addAttribute("isEdit", true);
        return "account-plan/form";
    }

    /**
     * アカウントプラン削除
     */
    @PostMapping("/delete")
    public String delete(@PathVariable Long customerId) {
        customerService.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("顧客が見つかりません: " + customerId));
        List<AccountPlan> plans = accountPlanService.findByCustomerId(customerId);
        for (AccountPlan plan : plans) {
            accountPlanService.deleteById(plan.getId());
        }
        return "redirect:/customers/" + customerId + "/account-plan";
    }
}
