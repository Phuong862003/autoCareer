package com.demo.autocareer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.InternshipSemester;
import com.demo.autocareer.model.Semester;
import com.demo.autocareer.model.Student;

@Repository
public interface InternshipSemesterRepository extends JpaRepository<InternshipSemester, Long>, JpaSpecificationExecutor<InternshipSemester>{
    Optional<InternshipSemester> findByStudent(Student student);
}
