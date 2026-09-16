package com.legaltrack.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        UserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getId() : null;
    }

    public static boolean hasRole(String roleName) {
        UserPrincipal principal = getCurrentUser();
        if (principal == null) return false;
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleName));
    }

    public static boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public static boolean isLawyer() {
        return hasRole("ROLE_LAWYER");
    }

    public static boolean isClient() {
        return hasRole("ROLE_CLIENT");
    }

    public static boolean isLegalAidOfficer() {
        return hasRole("ROLE_LEGAL_AID_OFFICER");
    }
}
