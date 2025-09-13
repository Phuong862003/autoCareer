package com.demo.autocareer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.StudentBehavior;

@Repository
public interface  StudentBehaviorRepository extends JpaRepository<StudentBehavior, Long>{
    
}
