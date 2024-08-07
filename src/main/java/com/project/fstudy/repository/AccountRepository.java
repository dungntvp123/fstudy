package com.project.fstudy.repository;

import com.project.fstudy.data.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByUserEmail(String email);

    boolean existsByUserEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT a.accountExpiredTime FROM Account a WHERE a.username = :username")
    Optional<Timestamp> findAccountExpiredTimeByUsername(@Param("username") String username);
}
