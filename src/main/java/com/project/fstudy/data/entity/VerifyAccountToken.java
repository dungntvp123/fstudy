package com.project.fstudy.data.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "verify_account_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyAccountToken {
    @Id
    private String id;
    private Integer accountId;
    private Instant expireTime;
}
