package vn.aicrm.viettelpost.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import lombok.RequiredArgsConstructor;
import vn.aicrm.exception.ViettelPostApiException;
import vn.aicrm.viettelpost.order.dto.*;
import vn.coreplatform.kernel.TenantContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createVTPOrder(@RequestBody CreateOrderRequest request) {
        
        try {
            ViettelPostResponse<ViettelPostOrderResponse> response = orderService.createVTPOrder(request);
            return ResponseEntity.ok(response);
            
        } catch (RestClientResponseException e) {
            
            String vtpErrorBody = e.getResponseBodyAsString();
            
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                "error", "Lỗi từ Viettel Post",
                "statusCode", e.getStatusCode().value(),
                "details", vtpErrorBody
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Lỗi hệ thống",
                "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId){
        OrderResponse response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<OrdersPageResponse<OrderSummaryResponse>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        UUID tenantId = TenantContext.current();
        
        OrdersPageResponse<OrderSummaryResponse> response = orderService.getAllOrders(tenantId, page, size);
        
        return ResponseEntity.ok(response);
    }
}