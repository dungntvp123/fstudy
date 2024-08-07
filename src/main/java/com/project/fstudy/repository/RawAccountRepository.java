package com.project.fstudy.repository;

import com.project.fstudy.data.entity.RawAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawAccountRepository extends MongoRepository<RawAccount, String> {
}
