package com.project.fstudy.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "UK_email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private int gender;
    private LocalDate dob;
    private String organization;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Folder rootFolder;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Folder binFolder;


    private LocalDate updateDate;
    @PreUpdate
    public void onUpdate() {
        updateDate = LocalDate.now();
    }
}
