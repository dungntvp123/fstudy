package com.project.fstudy.repository;

import com.project.fstudy.data.entity.VerifyAccountToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface VerifyAccountTokenRepository extends MongoRepository<VerifyAccountToken, String> {
    Optional<VerifyAccountToken> findByIdAndExpiredTimeAfter(String tokenId, Instant now);
}
