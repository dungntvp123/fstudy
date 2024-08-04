package com.project.fstudy.data.entity;

import com.project.fstudy.data.constant.TimeConstant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "username", name = "UK_username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Authority.class)
    @JoinTable(
            joinColumns = @JoinColumn(referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(referencedColumnName = "id")
    )
    private Set<Authority> authorities = new HashSet<>();
    private boolean isLocked;
    private Timestamp accountExpiredTime;
    private Timestamp credentialExpiredTime;
    private boolean isEnabled;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private User user;

    private LocalDate createDate;
    @PrePersist
    public void onCreate() {
        createDate = LocalDate.now();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public boolean isAccountNonExpired() {
        return accountExpiredTime.before(new Timestamp(TimeConstant.NOW));
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialExpiredTime.after(new Timestamp(TimeConstant.NOW));
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
