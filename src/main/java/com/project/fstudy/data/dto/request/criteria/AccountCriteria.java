package com.project.fstudy.data.dto.request.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCriteria {
    private String searchKey;
    private String sortField;
    private boolean isDescend;
    private Boolean isLocked;
    private Boolean isAccountExpired;
    private int pageIndex;
}
