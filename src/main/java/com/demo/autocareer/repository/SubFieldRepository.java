package com.demo.autocareer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.SubField;

@Repository
public interface SubFieldRepository extends JpaRepository<SubField, Long>{
    Optional<SubField> findById(Long id);
}
