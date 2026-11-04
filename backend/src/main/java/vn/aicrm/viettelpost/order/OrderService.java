package vn.aicrm.viettelpost.order;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.aicrm.enums.OrderStatus;
import vn.aicrm.exception.OrderException;
import vn.aicrm.exception.ViettelPostApiException;
import vn.aicrm.shared.RetailAccess;
import vn.aicrm.viettelpost.ShippingProviderService;
import vn.aicrm.viettelpost.dto.ShippingProviderResponse;
import vn.aicrm.viettelpost.order.dto.*;
import vn.aicrm.viettelpost.order.viettelpost.ViettelPostService;
import vn.coreplatform.audit.AuditService;
import vn.coreplatform.kernel.TenantContext;
import vn.coreplatform.permission.PermissionService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final JdbcTemplate jdbcTemplate;
    private final RetailAccess retailAccess;
    private final PermissionService permissionService;
    private final AuditService auditService;
    private final ShippingProviderService shippingProviderService;
    private final OrderRepository orderRepository;
    private final ViettelPostService viettelPostService;
    private final ObjectMapper objectMapper;

    @Transactional
    public ViettelPostResponse<ViettelPostOrderResponse> createVTPOrder(CreateOrderRequest request){
        
        UUID tenantId = TenantContext.current();
        if (tenantId == null) {
            throw new OrderException("Không xác định được Tenant. Vui lòng đăng nhập hoặc cung cấp API Key.");
        }

        ShippingProviderResponse shippingProvider = shippingProviderService.findById(request.shippingProviderId());
        UUID orderId = UUID.randomUUID();

        BaseOrderResponse baseOrderResponse = orderRepository.initOrder(orderId, request, tenantId, shippingProvider.senderName(), shippingProvider.senderPhone(), shippingProvider.senderAddress());

        OrderResponse orderResponse = orderRepository.findById(tenantId, orderId)
            .orElseThrow(()-> new OrderException("Không tìm thấy order!"));
        
        long moneyCollection = orderResponse.moneyCollection() != null ? orderResponse.moneyCollection() : 0L;
        int orderPayment = 1; 
        if(moneyCollection > 0){
            orderPayment = 3; 
        }

        ViettelPostOrderRequest viettelPostOrderRequest = new ViettelPostOrderRequest(
                orderResponse.orderCode(), 
                shippingProvider.senderName(), 
                shippingProvider.senderPhone(), 
                shippingProvider.senderAddress(), 
                orderResponse.receiverName(),
                orderResponse.receiverPhone(), 
                orderResponse.receiverAddress(), 
                orderResponse.productName(), 
                orderResponse.moneyCollection(),
                orderPayment, 
                "VTK"
        );
        
        ViettelPostResponse<ViettelPostOrderResponse>  response = viettelPostService.createViettelPostOrder(orderResponse.shippingProvidersId(), viettelPostOrderRequest);
        
        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(viettelPostOrderRequest);
        } catch (JsonProcessingException e) {
            requestJson = "{}";
        }
        if (response != null && Boolean.FALSE.equals(response.error()) && response.data() != null) {
            ViettelPostOrderResponse vtpData = response.data();
            
            String responseJson;
            try {
                responseJson = objectMapper.writeValueAsString(response);
            } catch (JsonProcessingException e) {
                responseJson = "{}";
            }

            String status = response.status() == 200 ? OrderStatus.SHIPPING.name() : OrderStatus.ERROR.name();

            orderRepository.updateVtpOrderInfo(
                                tenantId,
                                orderId,
                                vtpData.orderNumber(),          
                                "VTK",                   
                                vtpData.moneyTotal(),           
                                vtpData.moneyTotalFee(),        
                                vtpData.moneyFee(),             
                                vtpData.moneyCollectionFee(),   
                                vtpData.moneyVat(),             
                                vtpData.exchangeWeight(),       
                                vtpData.sortCode(),             
                                requestJson,
                                responseJson,
                                status
            );
            
            return response;
        } else {
            String errorMessage = (response != null && response.message() != null) 
                    ? response.message() : "Lỗi không xác định từ Viettel Post";
            throw new ViettelPostApiException("Tạo đơn VTP thất bại: " + errorMessage);
        }
    }

    public OrderResponse getOrder(UUID orderId){
        UUID tenantId = TenantContext.current();
        if (tenantId == null) throw new OrderException("Không xác định được Tenant");
        
        return orderRepository.findById(tenantId, orderId)
            .orElseThrow(() -> new  OrderException("Không tìm thấy đơn hàng"));
    }

    public OrdersPageResponse<OrderSummaryResponse> getAllOrders(UUID tenantId, int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        List<OrderSummaryResponse> orders = orderRepository.getAll(tenantId, page, size);
        long totalElements = orderRepository.countAll(tenantId);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new OrdersPageResponse<>(orders, page, size, totalElements, totalPages);
    }

    @Transactional
    public void processViettelPostWebhook(ViettelPostWebHookRequest request){
        String vtpOrderNumber = request.data().orderNumber();

        OrderResponse orderResponse = orderRepository.findByVtpOrderNumber(vtpOrderNumber)
                    .orElseThrow(() -> new OrderException("Nhận webhook cho mã vận đơn không tồn tại!"));
        
        OrderStatus systemStatus = syncOrderStatus(request.data().orderStatus());
        if(systemStatus == null){
            systemStatus = orderResponse.status();
        }
        
        String webhookReponse = "{}";
        try{
            webhookReponse = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Lỗi parse JSON webhook", e);
        }

        orderRepository.updateWebhookInfo(vtpOrderNumber, request.data().orderStatus(), webhookReponse, systemStatus);
        log.info("Đã xử lý webhook cho đơn {} - Trạng thái hệ thống: {}", vtpOrderNumber, systemStatus);
    }

    public UUID findTenantIdByApiKey(String apiKey) {
        try {
            String prefix = apiKey.substring(0, Math.min(apiKey.length(), 12));
            String hash = sha256(apiKey);
            
            return jdbcTemplate.queryForObject(
                "SELECT tenant_id FROM identity.api_key WHERE prefix = ? AND key_hash = ? AND status = 'ACTIVE' AND (expires_at IS NULL OR expires_at > now())",
                UUID.class, prefix, hash
            );
        } catch (Exception e) {
            log.warn("Không tìm thấy Tenant cho API Key: {}", e.getMessage());
            return null;
        }
    }

    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private OrderStatus syncOrderStatus(Integer status) {
        if (status == null) return null;

        return switch (status) {
            case 107, 201, 503, -15 -> OrderStatus.CANCELED;
            case 501 -> OrderStatus.DELIVERED;
            case 504 -> OrderStatus.RETURNED;
            case 100, 102, 103, 104, 105 -> OrderStatus.PICKING;
            case 200, 202, 300, 400, 500, 508, 509, 550 -> OrderStatus.SHIPPING;
            default -> null;
        };
    }
}