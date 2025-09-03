package com.demo.autocareer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.demo.autocareer.model.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long>{
    Optional<Semester> findById(Long id);
}
