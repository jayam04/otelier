package space.jayampatel.otelier.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationContext {
    
    /**
     * Get current authenticated user's ID
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return (String) authentication.getPrincipal();
        }
        
        return null;
    }
    
    /**
     * Get current user's role
     */
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getAuthorities() != null 
            && !authentication.getAuthorities().isEmpty()) {
            String authority = authentication.getAuthorities().iterator().next().getAuthority();
            return authority.replace("ROLE_", "").toLowerCase();
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
