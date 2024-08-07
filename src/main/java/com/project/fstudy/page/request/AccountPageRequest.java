package com.project.fstudy.page.request;


import com.project.fstudy.policy.StaticPolicy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public interface AccountPageRequest {
    default PageRequest inOrder(boolean isAscend, String field, int pageIndex) {
        if (isAscend) {
            return PageRequest.of(pageIndex, StaticPolicy.PAGE_SIZE, Sort.by(field).ascending());
        } else {
            return PageRequest.of(pageIndex, StaticPolicy.PAGE_SIZE, Sort.by(field).descending());
        }
    }
    default PageRequest inDefault(int pageIndex) {
        return PageRequest.of(pageIndex, StaticPolicy.PAGE_SIZE);
    }
}
