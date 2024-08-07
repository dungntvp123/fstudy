package com.project.fstudy.service;

import com.project.fstudy.data.dto.request.criteria.AccountCriteria;
import com.project.fstudy.page.AccountPageable;
import org.springframework.http.ResponseEntity;

public interface AccountManageService extends AccountPageable {
    public ResponseEntity<?> getAccount(AccountCriteria criteria);
    public ResponseEntity<?> getUserActionLog(); //extension
    public ResponseEntity<?> adjustLockAccount(Integer accountId);
    public ResponseEntity<?> setExpireToAccount(Integer accountId, Integer type);

}
