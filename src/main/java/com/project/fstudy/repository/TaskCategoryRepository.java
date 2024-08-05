package com.project.fstudy.repository;

import com.project.fstudy.data.entity.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory,String> {
}
