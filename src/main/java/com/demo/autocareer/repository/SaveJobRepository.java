package com.demo.autocareer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.SaveJob;
import com.demo.autocareer.model.Student;

@Repository
public interface SaveJobRepository extends JpaRepository<SaveJob, Long>, JpaSpecificationExecutor<SaveJob>{
    boolean existsByStudentAndJob(Student student, Job job);
    Optional<SaveJob> findByStudentAndJob(Student student, Job job);
}
