package com.legaltrack.repository;

import com.legaltrack.entity.LawyerProfile;
import com.legaltrack.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LawyerProfileRepository extends JpaRepository<LawyerProfile, Long> {
    Optional<LawyerProfile> findByUser(User user);
    Optional<LawyerProfile> findByUserId(Long userId);
    Optional<LawyerProfile> findByBarRegistrationNumber(String barRegistrationNumber);
    Boolean existsByBarRegistrationNumber(String barRegistrationNumber);

    @Query("SELECT DISTINCT lp FROM LawyerProfile lp " +
           "LEFT JOIN lp.availability la " +
           "LEFT JOIN lp.specializations ls " +
           "LEFT JOIN lp.courts lc " +
           "LEFT JOIN lp.languages ll " +
           "WHERE (:stateId IS NULL OR lp.state.id = :stateId) " +
           "AND (:districtId IS NULL OR lp.district.id = :districtId) " +
           "AND (:courtId IS NULL OR lc.court.id = :courtId) " +
           "AND (:specialization IS NULL OR LOWER(ls.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) " +
           "AND (:language IS NULL OR LOWER(ll.language) LIKE LOWER(CONCAT('%', :language, '%'))) " +
           "AND (:minExperience IS NULL OR lp.experienceYears >= :minExperience) " +
           "AND (:verifiedOnly IS NULL OR :verifiedOnly = FALSE OR lp.verified = TRUE) " +
           "AND (:availabilityStatus IS NULL OR la.status = :availabilityStatus)")
    Page<LawyerProfile> searchLawyers(
            @Param("stateId") Long stateId,
            @Param("districtId") Long districtId,
            @Param("courtId") Long courtId,
            @Param("specialization") String specialization,
            @Param("language") String language,
            @Param("minExperience") Integer minExperience,
            @Param("verifiedOnly") Boolean verifiedOnly,
            @Param("availabilityStatus") com.legaltrack.enums.LawyerAvailabilityStatus availabilityStatus,
            Pageable pageable
    );
}
