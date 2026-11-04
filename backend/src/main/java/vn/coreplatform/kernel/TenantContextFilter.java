package vn.coreplatform.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantContextFilter extends OncePerRequestFilter {

  private final JdbcTemplate jdbcTemplate;

  public TenantContextFilter(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
      throws ServletException, IOException {
    try {
      var authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getDetails() instanceof Map<?, ?> details && details.get("tenantId") instanceof UUID tenantId) {
        TenantContext.set(tenantId);
      } 
      else {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isBlank()) {
          try {
            // Tách prefix và tính hash
            String prefix = apiKey.substring(0, Math.min(apiKey.length(), 12));
            String hash = sha256(apiKey);

            // Query DB để tìm tenant_id
            UUID tenantId = jdbcTemplate.queryForObject(
                "SELECT tenant_id FROM identity.api_key WHERE prefix = ? AND key_hash = ? AND status = 'ACTIVE' AND (expires_at IS NULL OR expires_at > now())",
                UUID.class, prefix, hash
            );
            
            if (tenantId != null) {
              TenantContext.set(tenantId);
            }
          } catch (Exception e) {
          }
        }
      }
      
      chain.doFilter(request, response);
    } finally {
      TenantContext.clear();
    }
  }

  private String sha256(String value) {
    try {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}