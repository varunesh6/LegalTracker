package com.legaltrack.repository;

import com.legaltrack.entity.CourtComplex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtComplexRepository extends JpaRepository<CourtComplex, Long> {
    List<CourtComplex> findByDistrictId(Long districtId);
}
