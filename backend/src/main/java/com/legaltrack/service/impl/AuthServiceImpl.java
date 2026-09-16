package com.legaltrack.service.impl;

import com.legaltrack.dto.auth.*;
import com.legaltrack.entity.*;
import com.legaltrack.enums.LawyerAvailabilityStatus;
import com.legaltrack.enums.RoleType;
import com.legaltrack.enums.UserStatus;
import com.legaltrack.exception.ApiException;
import com.legaltrack.exception.DuplicateResourceException;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.mapper.UserMapper;
import com.legaltrack.repository.*;
import com.legaltrack.security.JwtTokenProvider;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.security.UserPrincipal;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final LawyerAvailabilityRepository lawyerAvailabilityRepository;
    private final LawyerSpecializationRepository lawyerSpecializationRepository;
    private final LawyerCourtRepository lawyerCourtRepository;
    private final LawyerLanguageRepository lawyerLanguageRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final CourtRepository courtRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail().trim(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String jwt = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = createRefreshToken(userPrincipal.getId());

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        User user = userRepository.findById(userPrincipal.getId()).orElse(null);
        auditLogService.logAction(user, "USER_LOGIN", "User", userPrincipal.getId(), null, "User logged in successfully");

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .id(userPrincipal.getId())
                .name(userPrincipal.getName())
                .email(userPrincipal.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerClient(RegisterClientRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("Email address is already in use: " + request.getEmail());
        }

        Role clientRole = roleRepository.findByName(RoleType.ROLE_CLIENT)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleType.ROLE_CLIENT));

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobile(request.getMobile())
                .status(UserStatus.ACTIVE)
                .roles(Set.of(clientRole))
                .build();

        user = userRepository.save(user);

        State state = request.getStateId() != null ? stateRepository.findById(request.getStateId()).orElse(null) : null;
        District district = request.getDistrictId() != null ? districtRepository.findById(request.getDistrictId()).orElse(null) : null;

        ClientProfile profile = ClientProfile.builder()
                .user(user)
                .address(request.getAddress())
                .state(state)
                .district(district)
                .pincode(request.getPincode())
                .occupation(request.getOccupation())
                .build();

        clientProfileRepository.save(profile);

        auditLogService.logAction(user, "CLIENT_REGISTRATION", "User", user.getId(), null, "New client account registered");

        UserPrincipal principal = UserPrincipal.create(user);
        String jwt = tokenProvider.generateTokenFromUserPrincipal(principal);
        RefreshToken refreshToken = createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(List.of(RoleType.ROLE_CLIENT.name()))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerLawyer(RegisterLawyerRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("Email address is already in use: " + request.getEmail());
        }

        if (lawyerProfileRepository.existsByBarRegistrationNumber(request.getBarRegistrationNumber().trim())) {
            throw new DuplicateResourceException("Bar registration number is already registered: " + request.getBarRegistrationNumber());
        }

        Role lawyerRole = roleRepository.findByName(RoleType.ROLE_LAWYER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleType.ROLE_LAWYER));

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobile(request.getMobile())
                .status(UserStatus.ACTIVE)
                .roles(Set.of(lawyerRole))
                .build();

        user = userRepository.save(user);

        State state = stateRepository.findById(request.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State", "id", request.getStateId()));
        District district = districtRepository.findById(request.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", request.getDistrictId()));

        LawyerProfile profile = LawyerProfile.builder()
                .user(user)
                .barRegistrationNumber(request.getBarRegistrationNumber().trim())
                .enrollmentYear(request.getEnrollmentYear())
                .experienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 0)
                .state(state)
                .district(district)
                .officeAddress(request.getOfficeAddress())
                .bio(request.getBio())
                .verified(false)
                .build();

        profile = lawyerProfileRepository.save(profile);

        // Availability
        LawyerAvailability availability = LawyerAvailability.builder()
                .lawyer(profile)
                .status(LawyerAvailabilityStatus.ACCEPTING_CLIENTS)
                .availableFrom(LocalDate.now())
                .availableUntil(LocalDate.now().plusYears(1))
                .build();
        lawyerAvailabilityRepository.save(availability);

        // Specializations
        if (request.getSpecializations() != null) {
            for (String spec : request.getSpecializations()) {
                lawyerSpecializationRepository.save(LawyerSpecialization.builder()
                        .lawyer(profile)
                        .specialization(spec.trim())
                        .build());
            }
        }

        // Courts
        if (request.getCourtIds() != null) {
            for (Long courtId : request.getCourtIds()) {
                courtRepository.findById(courtId).ifPresent(court -> {
                    lawyerCourtRepository.save(LawyerCourt.builder()
                            .lawyer(profile)
                            .court(court)
                            .build());
                });
            }
        }

        // Languages
        if (request.getLanguages() != null) {
            for (String lang : request.getLanguages()) {
                lawyerLanguageRepository.save(LawyerLanguage.builder()
                        .lawyer(profile)
                        .language(lang.trim())
                        .build());
            }
        }

        auditLogService.logAction(user, "LAWYER_REGISTRATION", "User", user.getId(), null, "New lawyer account registered");

        UserPrincipal principal = UserPrincipal.create(user);
        String jwt = tokenProvider.generateTokenFromUserPrincipal(principal);
        RefreshToken refreshToken = createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(List.of(RoleType.ROLE_LAWYER.name()))
                .build();
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserPrincipal principal = UserPrincipal.create(user);
                    String token = tokenProvider.generateTokenFromUserPrincipal(principal);
                    return TokenRefreshResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken)
                            .tokenType("Bearer")
                            .build();
                })
                .orElseThrow(() -> new ApiException("Refresh token is not in database or expired"));
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                refreshTokenRepository.deleteByUser(user);
                auditLogService.logAction(user, "USER_LOGOUT", "User", userId, null, "User logged out");
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new ApiException("No authenticated user found");
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        ClientProfile clientProfile = clientProfileRepository.findByUserId(currentUserId).orElse(null);
        LawyerProfile lawyerProfile = lawyerProfileRepository.findByUserId(currentUserId).orElse(null);

        return userMapper.toProfileDto(user, clientProfile, lawyerProfile);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ApiException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.logAction(user, "PASSWORD_CHANGE", "User", user.getId(), null, "User changed password");
    }

    private RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new ApiException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
