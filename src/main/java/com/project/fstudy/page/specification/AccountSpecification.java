package com.project.fstudy.page.specification;

import com.project.fstudy.data.constant.TimeConstant;
import com.project.fstudy.data.entity.Account;
import com.project.fstudy.data.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;

public interface AccountSpecification {
    default Specification<Account> hasUsernameLike(String key) {
        return (root, query, criteriaBuilder) -> {
           if (key == null) {
               return criteriaBuilder.conjunction();
           }
            return criteriaBuilder.like(root.get("username"), "%" + key + "%");
        };
    }

    default Specification<Account> hasEmailLike(String key) {
        return (root, query, criteriaBuilder) -> {
            if (key == null) {
                return criteriaBuilder.conjunction();
            }
            Join<User, Account> userAccountJoin = root.join("user");
            return criteriaBuilder.like(userAccountJoin.get("email"), "%"+key+"%");
        };
    }

    default Specification<Account> hasLocked(Boolean isLocked) {
        return (root, query, criteriaBuilder) -> {
            if (isLocked == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isLocked"), isLocked ? 0 : 1);
        };

    }

    default Specification<Account> hasAccountExpired(Boolean isAccountExpired) {
        return (root, query, criteriaBuilder) -> {
            if (isAccountExpired == null) {
                return criteriaBuilder.conjunction();
            }
            if (isAccountExpired) {
                return criteriaBuilder.greaterThan(root.get("accountExpiredTime"), new Timestamp(TimeConstant.NOW));
            } else {
                return criteriaBuilder.lessThan(root.get("accountExpiredTime"), new Timestamp(TimeConstant.NOW));
            }
        };

    }
}
