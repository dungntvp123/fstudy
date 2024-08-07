package com.project.fstudy.data.entity;

import com.project.fstudy.data.constant.TimeConstant;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "raw_accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RawAccount {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    @Indexed(unique = true)
    private String email;
    private Instant expiredTime;


}
