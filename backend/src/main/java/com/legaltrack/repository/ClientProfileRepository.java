package com.legaltrack.repository;

import com.legaltrack.entity.ClientProfile;
import com.legaltrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {
    Optional<ClientProfile> findByUser(User user);
    Optional<ClientProfile> findByUserId(Long userId);
}
