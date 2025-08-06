package com.demo.autocareer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Student;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


@Repository
public interface ApplyJobRepository extends JpaRepository<ApplyJob, Long>, JpaSpecificationExecutor<ApplyJob>{
    boolean existsByStudentAndJob(Student student, Job job);
    Page<ApplyJob> findByStudent(Student student, Pageable pageable);
}
