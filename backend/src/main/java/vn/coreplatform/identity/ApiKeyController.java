package vn.coreplatform.identity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.coreplatform.kernel.TenantContext;

@RestController
@RequestMapping("/api/v1/api-keys")
public class ApiKeyController {
    private final JdbcTemplate jdbc;

    public ApiKeyController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping
    public Map<String, String> createKey(@RequestBody CreateKeyRequest req) {
        UUID tenantId = TenantContext.current();
        if (tenantId == null) throw new RuntimeException("Chưa xác định được Tenant");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof Map<?, ?> details) || !(details.get("accountId") instanceof UUID accountId)) {
            throw new RuntimeException("Không xác định được người dùng tạo key");
        }

        // 2. Sinh Key ngẫu nhiên
        String rawKey = tenantId.toString().substring(0, 6) + UUID.randomUUID().toString().replace("-", "");
        String prefix = rawKey.substring(0, 12);
        String hash = sha256(rawKey);

        jdbc.update("""
            INSERT INTO identity.api_key (tenant_id, account_id, name, prefix, key_hash, status, expires_at)
            VALUES (?, ?, ?, ?, ?, 'ACTIVE', ?)
            """, 
            tenantId, 
            accountId, // <-- Bổ sung accountId
            req.name(), 
            prefix, 
            hash, 
            req.expiresInDays() != null ? OffsetDateTime.now().plusDays(req.expiresInDays()) : null
        );

        return Map.of("apiKey", rawKey, "message", "Hãy lưu key này ngay!");
    }

    // Lấy danh sách Key (Không trả về key_hash)
    @GetMapping
    public Object listKeys() {
        UUID tenantId = TenantContext.current();
        return jdbc.queryForList("""
            SELECT id, name, prefix, status, last_used_at, expires_at, created_at 
            FROM identity.api_key 
            WHERE tenant_id = ?
            ORDER BY created_at DESC
            """, tenantId);
    }

    @PatchMapping("/{id}/revoke")
    public void revokeKey(@PathVariable UUID id) {
        UUID tenantId = TenantContext.current();
        jdbc.update("UPDATE identity.api_key SET status = 'REVOKED' WHERE id = ? AND tenant_id = ?", id, tenantId);
    }

    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public record CreateKeyRequest(String name, Integer expiresInDays) {}
}