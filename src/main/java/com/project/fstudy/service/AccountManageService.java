package com.project.fstudy.service;

import com.project.fstudy.data.dto.request.criteria.AccountCriteria;
import org.springframework.http.ResponseEntity;

public interface AccountManageService {
    public ResponseEntity<?> getAccount(AccountCriteria criteria);
    public ResponseEntity<?> getUserActionLog(Integer accountId); //extension
    public ResponseEntity<?> adjustLockAccount(Integer accountId);
    public ResponseEntity<?> setExpireToAccount(Integer accountId, Integer type);

}
