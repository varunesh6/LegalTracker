package com.legaltrack.repository;

import com.legaltrack.entity.CaseHearing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CaseHearingRepository extends JpaRepository<CaseHearing, Long> {
    List<CaseHearing> findByCaseFileIdOrderByHearingDateDesc(Long caseId);
    List<CaseHearing> findByHearingDate(LocalDate date);
    List<CaseHearing> findByHearingDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT h FROM CaseHearing h WHERE h.hearingDate >= :today AND " +
           "(h.caseFile.client.id = :userId OR h.caseFile.lawyer.id = :userId) " +
           "ORDER BY h.hearingDate ASC")
    List<CaseHearing> findUpcomingHearingsForUser(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT h FROM CaseHearing h WHERE h.hearingDate >= :today ORDER BY h.hearingDate ASC")
    List<CaseHearing> findAllUpcomingHearings(@Param("today") LocalDate today);
}
