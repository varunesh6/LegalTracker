package com.legaltrack.repository;

import com.legaltrack.entity.LawyerRequest;
import com.legaltrack.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LawyerRequestRepository extends JpaRepository<LawyerRequest, Long> {
    Page<LawyerRequest> findByClientId(Long clientId, Pageable pageable);
    Page<LawyerRequest> findByLawyerId(Long lawyerId, Pageable pageable);
    List<LawyerRequest> findByLawyerIdAndStatus(Long lawyerId, RequestStatus status);
    Long countByLawyerIdAndStatus(Long lawyerId, RequestStatus status);
    Long countByClientIdAndStatus(Long clientId, RequestStatus status);
}
