package space.jayampatel.otelier.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthenticationContext {

    /**
     * Get current authenticated user's ID
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getSubject();
        }

        return null;
    }

    /**
     * Get current user's role
     */
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();

            // Try to find role in common claims
            if (jwt.hasClaim("role")) {
                return jwt.getClaimAsString("role");
            } else if (jwt.hasClaim("roles")) {
                List<String> roles = jwt.getClaimAsStringList("roles");
                if (roles != null && !roles.isEmpty()) {
                    return roles.get(0); // Return first role
                }
            }
        }

        return "user";
    }

    /**
     * Check if current user has specific role
     */
    public boolean hasRole(String role) {
        return role.equalsIgnoreCase(getCurrentUserRole());
    }

    /**
     * Check if current user has any of the specified roles
     */
    public boolean hasAnyRole(String... roles) {
        String currentRole = getCurrentUserRole();

        for (String role : roles) {
            if (role.equalsIgnoreCase(currentRole)) {
                return true;
            }
        }

        return false;
    }
}