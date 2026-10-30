package vn.aicrm.viettelpost;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import vn.aicrm.viettelpost.dto.ShippingProviderResponse;
import vn.aicrm.viettelpost.dto.UpdateAddressRequest;

@Repository
public class ShippingProviderRepository {
    private final JdbcTemplate jdbcTemplate;

    public ShippingProviderRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ShippingProviderResponse> getAll(UUID tenantId) {
        String sql = """
            SELECT id, code, name, token, is_enabled, sender_name, sender_phone, sender_address, is_configured, token_expires_at
            FROM retail.shipping_providers
            WHERE tenant_id = ?
            ORDER BY id ASC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new ShippingProviderResponse(
                rs.getLong("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("token"),
                rs.getBoolean("is_enabled"),
                rs.getString("sender_name"),
                rs.getString("sender_phone"),
                rs.getString("sender_address"),
                rs.getTimestamp("token_expires_at")
            ),
            tenantId
        );
    }

    public int insertToken(UUID tenantId, UUID actorId, String token, Long id) {
        String sql = """
            UPDATE retail.shipping_providers
            SET token = ?, 
                token_expires_at = ?,
                is_configured = TRUE,
                updated_by = ?,
                updated_at = NOW()
            WHERE tenant_id = ? AND id = ?
            """;
            
        OffsetDateTime expiresAt = OffsetDateTime.now().plusYears(1);
        
        return jdbcTemplate.update(
            sql, 
            token, 
            Timestamp.from(expiresAt.toInstant()), 
            actorId, 
            tenantId, 
            id
        );
    }

    public boolean toggleShippingProvider(UUID tenantId, UUID actorId, Long id, Boolean isEnabled) {
        String sql = """
            UPDATE retail.shipping_providers
            SET is_enabled = ?, 
                updated_by = ?,
                updated_at = NOW()
            WHERE tenant_id = ? AND id = ?
            """;
            
        int updatedRows = jdbcTemplate.update(
            sql, 
            isEnabled, 
            actorId, 
            tenantId, 
            id
        );
        
        return updatedRows > 0;
    }

    public Long insert(
            UUID tenantId,
            UUID actorId,
            String code,
            String name,
            String senderName,
            String senderPhone,
            String senderAddress
    ) {
        String sql = """
            INSERT INTO retail.shipping_providers (
                tenant_id, code, name, sender_name, sender_phone, sender_address,
                is_enabled, is_configured, updated_by
            )
            VALUES (?, ?, ?, ?, ?, ?, FALSE, FALSE, ?)
            RETURNING id
            """;

        return jdbcTemplate.queryForObject(
                sql, Long.class,
                tenantId, code, name, senderName, senderPhone, senderAddress, actorId
        );
    }

    public int updateSenderAddress(
            UUID tenantId, Long providerId, UpdateAddressRequest request, UUID actorId
    ) {
        String sql = """
            UPDATE retail.shipping_providers
            SET sender_name = ?, sender_phone = ?, sender_address = ?,
                updated_by = ?, updated_at = NOW()
            WHERE tenant_id = ? AND id = ?
            """;

        return jdbcTemplate.update(
                sql,
                request.senderName().trim(),
                request.senderPhone().trim(),
                request.senderAddress().trim(),
                actorId, tenantId, providerId
        );
    }

    public ShippingProviderResponse getShippingProvider(UUID tenantId, Long providerId) {
        String sql = """
            SELECT id, code, name, token, is_enabled, sender_name, sender_phone, sender_address, token_expires_at
            FROM retail.shipping_providers
            WHERE tenant_id = ? AND id = ?
            """;

        return jdbcTemplate.query(
                sql,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    return new ShippingProviderResponse(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("token"),         
                            rs.getBoolean("is_enabled"),      
                            rs.getString("sender_name"),   
                            rs.getString("sender_phone"),
                            rs.getString("sender_address"),
                            rs.getTimestamp("token_expires_at")
                    );
                },
                tenantId, providerId
        );
    }
}