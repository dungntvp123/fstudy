package com.project.fstudy.controller;

import com.project.fstudy.data.dto.request.criteria.AccountCriteria;
import com.project.fstudy.service.AccountManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountManageService accountManageService;

    @PatchMapping("/adjust-lock/{accountId}")
    public ResponseEntity<?> adjustLock(@PathVariable("accountId") Integer accountId) {
        return accountManageService.adjustLockAccount(accountId);
    }

    @PatchMapping("/set-account-expired/{accountId}")
    public ResponseEntity<?> setAccountExpiredTime(@PathVariable("accountId") Integer accountId,
                                                   @RequestParam("type") Integer type) {
        return accountManageService.setExpireToAccount(accountId, type);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAccount(AccountCriteria criteria) {
        return accountManageService.getAccount(criteria);
    }
}
