package com.example.crm.controller;

import com.example.crm.model.Customer;
import com.example.crm.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 顧客管理コントローラー
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 顧客一覧表示
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "list";
    }

    /**
     * 新規登録フォーム表示
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("isEdit", false);
        return "form";
    }

    /**
     * 編集フォーム表示
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("顧客が見つかりません: " + id));
        model.addAttribute("customer", customer);
        model.addAttribute("isEdit", true);
        return "form";
    }

    /**
     * 顧客登録・更新処理
     */
    @PostMapping("/save")
    public String save(@ModelAttribute Customer customer) {
        customerService.save(customer);
        return "redirect:/customers";
    }

    /**
     * 顧客削除処理
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        customerService.deleteById(id);
        return "redirect:/customers";
    }
}
