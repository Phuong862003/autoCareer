package com.demo.autocareer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.Faculty;
import java.util.List;
import java.util.Optional;


@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long>{
    Optional<Faculty> findByFacultyName(String faculty_name);
}
