package com.project.fstudy.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String categoryName;
    private int remindBeforeDays;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy = "taskCategory")
    private Set<Task> task;
}
