package space.jayampatel.otelier.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.issuer}")
    private String issuer;
    
    /**
     * Parse and validate JWT token
     */
    public Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Extract user ID from token
     */
    public String getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject(); // 'sub' claim contains userId
    }
    
    /**
     * Extract user role from token
     */
    public String getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        
        // Supabase stores role in 'role' claim
        String role = claims.get("role", String.class);
        
        // If not found, try 'user_role' or other common claims
        if (role == null) {
            role = claims.get("user_role", String.class);
        }
        
        return role != null ? role : "user"; // default to 'user' if no role found
    }
    
    /**
     * Check if token has expired
     */
    public boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        return claims.getExpiration().before(new Date());
    }
    
    /**
     * Check if user has required role
     */
    public boolean hasRole(String token, String... allowedRoles) {
        String userRole = getRoleFromToken(token);
        
        for (String role : allowedRoles) {
            if (role.equalsIgnoreCase(userRole)) {
                return true;
            }
        }
        
        return false;
    }
}
