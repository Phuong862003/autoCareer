package com.demo.autocareer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.District;
import com.demo.autocareer.model.Role;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long>{
    Optional<District> findById(Long id);
    List<District> findByProvinceId(Long id);
}
