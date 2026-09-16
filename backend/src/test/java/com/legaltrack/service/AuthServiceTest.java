package com.legaltrack.service;

import com.legaltrack.dto.auth.AuthResponse;
import com.legaltrack.dto.auth.LoginRequest;
import com.legaltrack.dto.auth.RegisterClientRequest;
import com.legaltrack.entity.RefreshToken;
import com.legaltrack.entity.Role;
import com.legaltrack.entity.User;
import com.legaltrack.enums.RoleType;
import com.legaltrack.enums.UserStatus;
import com.legaltrack.exception.DuplicateResourceException;
import com.legaltrack.repository.*;
import com.legaltrack.security.JwtTokenProvider;
import com.legaltrack.security.UserPrincipal;
import com.legaltrack.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private ClientProfileRepository clientProfileRepository;

    @Mock
    private LawyerProfileRepository lawyerProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private Role clientRole;

    @BeforeEach
    void setUp() {
        clientRole = Role.builder().id(1L).name(RoleType.ROLE_CLIENT).build();
        testUser = User.builder()
                .id(100L)
                .email("client@example.com")
                .password("encoded_pass")
                .name("Test Client")
                .mobile("9876543210")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(clientRole))
                .build();
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("client@example.com");
        request.setPassword("password123");

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = UserPrincipal.create(testUser);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("mock_jwt_access_token");
        when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(testUser)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(604800000))
                .build();
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock_jwt_access_token", response.getAccessToken());
        assertEquals("client@example.com", response.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should fail login with invalid password")
    void testLoginInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("client@example.com");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Should register new client successfully")
    void testRegisterClientSuccess() {
        RegisterClientRequest request = new RegisterClientRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setName("New User");
        request.setMobile("9998887776");

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleType.ROLE_CLIENT)).thenReturn(Optional.of(clientRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
        when(tokenProvider.generateTokenFromUserPrincipal(any(UserPrincipal.class))).thenReturn("mock_token");

        RefreshToken refreshToken = RefreshToken.builder()
                .user(testUser)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(604800000))
                .build();
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        AuthResponse result = authService.registerClient(request);

        assertNotNull(result);
        assertEquals("client@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(clientProfileRepository).save(any());
    }

    @Test
    @DisplayName("Should reject duplicate email registration")
    void testRegisterDuplicateEmail() {
        RegisterClientRequest request = new RegisterClientRequest();
        request.setEmail("client@example.com");
        request.setPassword("password123");
        request.setName("Duplicate Client");

        when(userRepository.existsByEmail("client@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.registerClient(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
