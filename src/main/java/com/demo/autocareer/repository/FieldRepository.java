package com.demo.autocareer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.Field;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long>{
    @Query("SELECT DISTINCT f FROM Field f LEFT JOIN FETCH f.subfields")
    List<Field> findAllWithSubfields();

    @Query("""
    SELECT DISTINCT f
    FROM Field f
    LEFT JOIN FETCH f.subfields
    WHERE f.id = :fieldId
    """)
    Optional<Field> findByIdWithSubfields(@Param("fieldId") Long fieldId);

}
