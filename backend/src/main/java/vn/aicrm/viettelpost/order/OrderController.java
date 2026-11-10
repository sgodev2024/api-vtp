package vn.aicrm.viettelpost.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;

import lombok.RequiredArgsConstructor;
import vn.aicrm.viettelpost.order.dto.*;
import vn.coreplatform.kernel.TenantContext;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    private final OrderService orderService;

    private UUID resolveTenantId(String apiKey) {
        // 1. Ưu tiên JWT Token
        UUID tenantId = TenantContext.current();
        if (tenantId != null) {
            return tenantId;
        }
        
        // 2. Nếu không có, thử lấy từ API Key
        if (apiKey != null && !apiKey.isBlank()) {
            return orderService.findTenantIdByApiKey(apiKey);
        }
        
        return null;
    }

    @PostMapping
    public ResponseEntity<?> initOrder(
            @RequestBody CreateOrderRequest request, 
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        UUID tenantId = resolveTenantId(apiKey);
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Chưa xác định được Tenant. Vui lòng cung cấp JWT hoặc X-API-Key."));
        }  
        
        try {
            TenantContext.set(tenantId);
            BaseOrderResponse baseOrderResponse = orderService.initVtpOrder(request);
            
            if (baseOrderResponse != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(baseOrderResponse);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Không thể khởi tạo đơn hàng. Vui lòng kiểm tra lại trường dữ liệu!"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Lỗi hệ thống",
                "details", e.getMessage()
            ));
        } finally {
            TenantContext.clear(); 
        }
    }

    @PostMapping("/{orderId}/vtp-service")
    public ResponseEntity<?> getAvailableVtpService(
            @PathVariable UUID orderId,
            @RequestParam Long shippingProviderId) {
        
        // Lấy tenantId từ JWT Token (Admin đã đăng nhập)
        UUID tenantId = TenantContext.current();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Chưa xác định được Tenant. Vui lòng đăng nhập."));
        }

        try {
            GetViettelPostServiceResponse response = orderService.getVtpService(orderId, shippingProviderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Lỗi hệ thống",
                "details", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/vtp")
    public ResponseEntity<?> createVTPOrder(
            @PathVariable UUID orderId,
            @RequestParam String vtpOrderService) {
        
        UUID tenantId = TenantContext.current();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Chưa xác định được Tenant. Vui lòng đăng nhập."));
        }

        try {
            // Gọi service với orderId và mã dịch vụ (VD: "VTK", "VCN")
            ViettelPostResponse<ViettelPostOrderResponse> response = orderService.createVTPOrder(orderId, vtpOrderService);
            return ResponseEntity.ok(response);
            
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                "error", "Lỗi từ Viettel Post",
                "statusCode", e.getStatusCode().value(),
                "details", e.getResponseBodyAsString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Lỗi hệ thống",
                "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable UUID orderId,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        UUID tenantId = resolveTenantId(apiKey);
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TenantContext.set(tenantId);
            OrderResponse response = orderService.getOrder(orderId);
            return ResponseEntity.ok(response);
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        UUID tenantId = resolveTenantId(apiKey);
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OrdersPageResponse<OrderSummaryResponse> response = orderService.getAllOrders(tenantId, page, size);
        return ResponseEntity.ok(response);
    }
}