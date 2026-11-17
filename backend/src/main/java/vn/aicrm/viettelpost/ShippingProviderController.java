package vn.aicrm.viettelpost;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.aicrm.viettelpost.dto.*;
import vn.coreplatform.kernel.TenantContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shipping-providers")
public class ShippingProviderController {

    private final ShippingProviderService shippingProviderService;
    private final ViettelPostTokenService viettelPostTokenService;
    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    @Value("${viettelpost.webhook.secret:}")
    private String vtpSecret;

    // Hàm hỗ trợ lấy Tenant ID
    private UUID resolveTenantId(String apiKey) {
        UUID tenantId = TenantContext.current();
        
        if (tenantId != null) return tenantId; // Ưu tiên JWT Token
        
        if (apiKey != null && !apiKey.isBlank()) {
            return shippingProviderService.findTenantIdByApiKey(apiKey);
        }
        return null;
    }

    @PostMapping
    public ResponseEntity<?> addShippingProvider(@RequestBody AddShippingProviderRequest request) {
        return ResponseEntity.ok(shippingProviderService.addShippingProvider(request));
    }

    @PutMapping("/{id}/token")
    public ResponseEntity<Void> insertToken(@PathVariable Long id, @RequestBody UpdateTokenRequest request) {
        viettelPostTokenService.insertToken(request.token(), id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateShippingProviderStatusRequest request) {
        shippingProviderService.updateEnabled(id, request.isEnabled());
        return ResponseEntity.noContent().build();
    }

    // SỬA API GET ALL
    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(value = "X-API-Key", required = false) String apiKey){
        UUID tenantId = resolveTenantId(apiKey);
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Chưa xác định được Tenant. Vui lòng cung cấp JWT hoặc X-API-Key.");
        }
        
        // Set context để các hàm bên dưới dùng
        TenantContext.set(tenantId);
        try {
            List<ShippingProviderResponse> response = shippingProviderService.getAll();
            return ResponseEntity.ok(response);
        } finally {
            TenantContext.clear();
        }
    }
    
    // SỬA API GET BY ID
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getProvider(
            @PathVariable Long id,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        UUID tenantId = resolveTenantId(apiKey);

        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TenantContext.set(tenantId);

        try {
            return ResponseEntity.ok(shippingProviderService.findById(id));
        } finally {
            TenantContext.clear();
        }
    }

    @PutMapping("/{id}/address")
    public ResponseEntity<Integer> updateSenderInfo(@RequestBody @Valid UpdateAddressRequest request, @PathVariable Long id){
        return ResponseEntity.ok(shippingProviderService.updateSenderInfo(request, id));
    }

    /**
     * Lấy URL webhook Viettel Post dùng chung cho tenant
     */
    @GetMapping("/webhook-url")
    public ResponseEntity<?> getWebhookUrl(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        UUID tenantId = resolveTenantId(apiKey);

        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Chưa xác định được Tenant");
        }

        TenantContext.set(tenantId);

        try {
            if (!StringUtils.hasText(publicBaseUrl)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Chưa cấu hình app.public-base-url");
            }

            String baseUrl = publicBaseUrl.replaceAll("/+$", "");

            String webhookUrl = baseUrl + "/api/v1/orders/webhook/vtp";

            return ResponseEntity.ok(Map.of(
                    "webhookUrl", webhookUrl,
                    "method", "POST",
                    "contentType", "application/json"
            ));

        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/vtp-secret")
    public ResponseEntity<?> getVtpSecret(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        UUID tenantId = resolveTenantId(apiKey);

        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Chưa xác định được Tenant");
        }

        TenantContext.set(tenantId);

        try {
            if (!StringUtils.hasText(vtpSecret)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Chưa cấu hình VIETTELPOST_WEBHOOK_SECRET");
            }

            return ResponseEntity.ok(Map.of(
                    "webhookSecret", vtpSecret
            ));

        } finally {
            TenantContext.clear();
        }
    }
}