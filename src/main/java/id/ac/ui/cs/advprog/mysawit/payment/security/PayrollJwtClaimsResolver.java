package id.ac.ui.cs.advprog.mysawit.payment.security;

import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollAuthenticationException;
import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollAuthorizationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PayrollJwtClaimsResolver {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_CLAIM = "role";
    private static final String NAME_CLAIM = "name";
    private static final String NAMA_CLAIM = "nama";
    
    private final javax.crypto.SecretKey secretKey;

    public PayrollJwtClaimsResolver(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Resolve claims for worker submission (WORKER role only)
     */
    public PayrollSubmissionContext resolve(String authorizationHeader) {
        Map<String, Object> claims = extractAndValidateClaims(authorizationHeader);
        String role = (String) claims.get(ROLE_CLAIM);
        
        if (!isKnownMysawitRole(role)) {
            throw new PayrollAuthorizationException(
                    "User role is not authorized to create payroll");
        }

        String workerId = extractUserId(claims);
        String workerName = extractDisplayName(claims);
        
        return new PayrollSubmissionContext(workerId, workerName, role);
    }

    /**
     * Resolve claims for payroll approval (ADMIN role only)
     */
    public PayrollApprovalContext resolveApprover(String authorizationHeader) {
        Map<String, Object> claims = extractAndValidateClaims(authorizationHeader);
        String role = (String) claims.get(ROLE_CLAIM);
        
        if (role == null || !role.equals("ADMIN")) {
            throw new PayrollAuthorizationException(
                    "Only ADMIN role can approve payroll");
        }

        String userId = extractUserId(claims);
        String name = extractDisplayName(claims);
        
        return new PayrollApprovalContext(userId, role, name);
    }

    /**
     * Resolve claims for payroll viewing (WORKER, ADMIN roles allowed)
     */
    public PayrollViewerContext resolveViewer(String authorizationHeader) {
        Map<String, Object> claims = extractAndValidateClaims(authorizationHeader);
        String role = (String) claims.get(ROLE_CLAIM);
        
        if (role == null || (!role.equals("WORKER") && !role.equals("ADMIN") 
                && !role.equals("BURUH") && !role.equals("MANDOR"))) {
            throw new PayrollAuthorizationException(
                    "User role is not authorized to view payroll");
        }

        String userId = extractUserId(claims);
        return new PayrollViewerContext(userId, role);
    }

    /**
     * Extract Bearer token from Authorization header and parse JWT claims
     */
    private Map<String, Object> extractAndValidateClaims(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new PayrollAuthenticationException(
                    "Authorization header is required");
        }

        String token = extractBearerToken(authorizationHeader);
        return parseSignedClaims(token);
    }

    /**
     * Extract Bearer token from Authorization header
     */
    private String extractBearerToken(String authorizationHeader) {
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new PayrollAuthenticationException(
                    "Missing or invalid JWT token");
        }
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    /**
     * Parse and validate JWT signature
     */
    private Map<String, Object> parseSignedClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new PayrollAuthenticationException(
                    "Missing or invalid JWT token");
        }
    }

    /**
     * Extract user ID from claims, supporting multiple field names for compatibility
     */
    private String extractUserId(Map<String, Object> claims) {
        String userId = (String) claims.get("sub");
        if (userId == null) {
            userId = (String) claims.get("userId");
        }
        if (userId == null) {
            userId = (String) claims.get("workerId");
        }
        if (userId == null) {
            userId = (String) claims.get("buruhId");
        }
        if (userId == null) {
            userId = (String) claims.get("adminId");
        }

        if (userId == null || userId.isBlank()) {
            throw new PayrollAuthenticationException(
                    "Missing user identifier in JWT token");
        }

        return userId;
    }

    private String extractDisplayName(Map<String, Object> claims) {
        Object name = claims.get(NAME_CLAIM);
        if (name == null) {
            name = claims.get(NAMA_CLAIM);
        }
        if (name == null) {
            name = claims.get("username");
        }
        return name == null ? null : String.valueOf(name);
    }

    private boolean isKnownMysawitRole(String role) {
        return role != null
                && (role.equals("WORKER")
                || role.equals("BURUH")
                || role.equals("MANDOR")
                || role.equals("SUPIR")
                || role.equals("ADMIN"));
    }
}
