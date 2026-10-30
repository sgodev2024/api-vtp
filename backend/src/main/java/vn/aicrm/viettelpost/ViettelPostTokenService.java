package vn.aicrm.viettelpost;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import vn.aicrm.shared.RetailAccess;
import vn.aicrm.viettelpost.dto.TokenResponse;
import vn.coreplatform.audit.AuditService;
import vn.coreplatform.permission.PermissionService;

@Service
public class ViettelPostTokenService {
    
    private final JdbcTemplate jdbcTemplate;
    private final ShippingProviderRepository shippingProviderRepository;
    private final RetailAccess retailAccess;
    private final PermissionService permissionService;
    private final AuditService auditService;

    public ViettelPostTokenService(JdbcTemplate jdbcTemplate, RetailAccess retailAccess, PermissionService permissionService, AuditService auditService, ShippingProviderRepository shippingProviderRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.retailAccess = retailAccess;
        this.permissionService = permissionService;
        this.auditService = auditService;
        this.shippingProviderRepository = shippingProviderRepository;
    }

    @Transactional
    public void insertToken(String token, Long id){

        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token không được để trống");
        }       

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }
        
        UUID tenantId = retailAccess.tenant(authentication);
        UUID actorId = retailAccess.actor(authentication);

        permissionService.require(authentication, "shipping", "IMPORT", null);

        int updatedToken = shippingProviderRepository.insertToken(tenantId, actorId, token, id);
        if (updatedToken == 0) {
            throw new RuntimeException("Không tìm thấy nhà vận chuyển hoặc bạn không có quyền cập nhật ID này");
        }
        
        auditService.record(tenantId.toString(), actorId, actorId.toString(), "UPDATE_TOKEN", "aicrm-shipping", null, "SUCCESS", null);
    }

    public TokenResponse getToken(Long id, UUID tenantId) {
        if (tenantId == null) {
            throw new IllegalStateException(
                "Không xác định được tenant của request"
            );
        }

        if (id == null) {
            throw new IllegalArgumentException(
                "Shipping provider ID không được để trống"
            );
        }

        String sql = """
            SELECT token, token_expires_at
            FROM retail.shipping_providers
            WHERE tenant_id = ?
              AND id = ?
              AND is_enabled = TRUE
              AND is_configured = TRUE
            """;

        TokenResponse tokenResponse;

        try {
            tokenResponse = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    var token = rs.getString("token");
                    var expiresAt = rs.getObject(
                        "token_expires_at",
                        OffsetDateTime.class
                    );

                    return new TokenResponse(token, expiresAt);
                },
                tenantId,
                id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalStateException(
                "Không tìm thấy nhà vận chuyển đang hoạt động "
                    + "hoặc chưa được cấu hình cho tenant này",
                e
            );
        }

        if (tokenResponse == null
                || !StringUtils.hasText(tokenResponse.token())) {
            throw new IllegalStateException(
                "Token của nhà vận chuyển đang bị trống"
            );
        }

        return tokenResponse;
    }
}
