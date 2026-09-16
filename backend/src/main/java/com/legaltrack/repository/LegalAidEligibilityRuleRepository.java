package com.legaltrack.repository;

import com.legaltrack.entity.LegalAidEligibilityRule;
import com.legaltrack.enums.LegalAidCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegalAidEligibilityRuleRepository extends JpaRepository<LegalAidEligibilityRule, Long> {
    List<LegalAidEligibilityRule> findByStateIdAndActiveTrue(Long stateId);
    Optional<LegalAidEligibilityRule> findByStateIdAndCategoryAndActiveTrue(Long stateId, LegalAidCategory category);
}
