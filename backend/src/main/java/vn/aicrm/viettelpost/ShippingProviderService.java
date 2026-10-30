package vn.aicrm.viettelpost;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import vn.aicrm.shared.RetailAccess;
import vn.aicrm.viettelpost.*;
import vn.aicrm.viettelpost.dto.AddShippingProviderRequest;
import vn.aicrm.viettelpost.dto.ShippingProviderResponse;
import vn.aicrm.viettelpost.dto.UpdateAddressRequest;
import vn.coreplatform.audit.AuditService;
import vn.coreplatform.kernel.TenantContext;
import vn.coreplatform.permission.PermissionService;

import java.util.List;
import java.util.Locale;
import java.util.UUID;


@Service
public class ShippingProviderService {
    
    private final JdbcTemplate jdbcTemplate;
    private final ShippingProviderRepository shippingProviderRepository;
    private final RetailAccess retailAccess;
    private final PermissionService permissionService;
    private final AuditService auditService;

    public ShippingProviderService(JdbcTemplate jdbcTemplate, RetailAccess retailAccess, PermissionService permissionService, AuditService auditService, ShippingProviderRepository shippingProviderRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.retailAccess = retailAccess;
        this.permissionService = permissionService;
        this.auditService = auditService;
        this.shippingProviderRepository = shippingProviderRepository;
    }

    public List<ShippingProviderResponse> getAll(){
        UUID tenantId  = TenantContext.current();
        List<ShippingProviderResponse> responses = shippingProviderRepository.getAll(tenantId);
        return responses;
    }

    @Transactional
    public void updateEnabled(Long id, Boolean isEnabled) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID tenantId = retailAccess.tenant(authentication);
        UUID actorId = retailAccess.actor(authentication);

        permissionService.require(authentication, "shipping", "UPDATE", null);


        boolean isUpdated = shippingProviderRepository.toggleShippingProvider(tenantId, actorId, id, isEnabled);

        if (!isUpdated) {
            throw new RuntimeException("Không tìm thấy nhà vận chuyển hoặc bạn không có quyền thao tác.");
        }

        // auditService.record(tenantId, actorId.toString(), null, "Update nable service", null, null, "Success", null);
    }

    @Transactional
    public ShippingProviderResponse addShippingProvider(
            AddShippingProviderRequest request
    ) {
        if (!StringUtils.hasText(request.code())
                || !StringUtils.hasText(request.name())) {
            throw new IllegalArgumentException(
                    "Mã (code) và Tên (name) không được để trống"
            );
        }

        if (!StringUtils.hasText(request.senderName())
                || !StringUtils.hasText(request.senderPhone())
                || !StringUtils.hasText(request.senderAddress())) {
            throw new IllegalArgumentException(
                    "Tên, số điện thoại và địa chỉ người gửi không được để trống"
            );
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UUID tenantId = retailAccess.tenant(authentication);
        UUID actorId = retailAccess.actor(authentication);

        permissionService.require(
                authentication, "shipping", "CREATE", null
        );

        String normalizedCode =
                request.code().trim().toUpperCase(Locale.ROOT);

        String trimmedName = request.name().trim();
        String senderName = request.senderName().trim();
        String senderPhone = request.senderPhone().trim();
        String senderAddress = request.senderAddress().trim();

        try {
            Long newId = shippingProviderRepository.insert(
                    tenantId, actorId, normalizedCode, trimmedName,
                    senderName, senderPhone, senderAddress
            );

            return new ShippingProviderResponse(
                    newId,
                    normalizedCode,
                    trimmedName,
                    null,                
                    false,                 
                    request.senderName(),
                    request.senderPhone(),
                    request.senderAddress(),
                    null                   
        );

        } catch (DuplicateKeyException e) {
            throw new RuntimeException(
                    "Mã nhà vận chuyển '" + normalizedCode
                            + "' đã tồn tại trên hệ thống.",
                    e
            );
        }
    }

    @Transactional
    public int updateSenderInfo(UpdateAddressRequest request, Long providerId){
        UUID tenantId = TenantContext.current();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID actorID = retailAccess.actor(authentication);
        
        permissionService.require(authentication, "shipping", "UPDATE",actorID);

        int updated = shippingProviderRepository.updateSenderAddress(tenantId, providerId, request, actorID);

        if (updated == 0) {
            throw new RuntimeException(
                    "Không tìm thấy nhà vận chuyển hoặc bạn không có quyền cập nhật"
            );
        }
        return updated;
    } 

    public ShippingProviderResponse findById(Long providerId){
        UUID tenantId = TenantContext.current();
        return shippingProviderRepository.getShippingProvider(tenantId, providerId);
    }
    
}