package vn.aicrm.viettelpost.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import vn.aicrm.enums.OrderStatus;
import vn.aicrm.viettelpost.order.dto.BaseOrderResponse;
import vn.aicrm.viettelpost.order.dto.CreateOrderRequest;
import vn.aicrm.viettelpost.order.dto.OrderResponse;
import vn.aicrm.viettelpost.order.dto.OrderSummaryResponse;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public BaseOrderResponse initOrder(
            UUID orderId,
            CreateOrderRequest request,
            UUID tenantId 
    ) {
        String orderCode = "ORD-" + orderId.toString().substring(0, 8).toUpperCase();

        String sql = """
            INSERT INTO retail.orders (
                id, tenant_id, order_code, status, total_amount, product_name,
                receiver_name, receiver_phone, receiver_address,
                money_collection
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING
                id, order_code, status,
                receiver_name, receiver_phone, receiver_address,
                product_name, total_amount, money_collection
            """;

        return jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new BaseOrderResponse(
                rs.getObject("id", UUID.class),
                rs.getString("order_code"),
                rs.getString("status"),
                rs.getString("receiver_name"),
                rs.getString("receiver_phone"),
                rs.getString("receiver_address"),
                rs.getString("product_name"),
                rs.getBigDecimal("total_amount"),
                rs.getBigDecimal("money_collection")
            ),
            orderId,                          // 1. id
            tenantId,                         // 2. tenant_id
            orderCode,                        // 3. order_code
            "PENDING",                        // 4. status
            request.totalPrice(),             // 5. total_amount
            request.productName(),            // 6. product_name
            request.receiveFullName(),        // 7. receiver_name
            request.receiverPhone(),          // 8. receiver_phone
            request.receiverAddress(),        // 9. receiver_address
            request.moneyCollection()         // 10. money_collection
        );
    }

        public Optional<OrderResponse> findById(UUID tenantId, UUID orderId) {
        String sql = """
            SELECT 
                id, tenant_id, order_code, status, shipping_providers_id, total_amount, product_name,
                receiver_name, receiver_phone, receiver_address,
                sender_full_name, sender_phone, sender_address,
                vtp_service_id, vtp_order_number, vtp_order_status, 
                money_collection, money_total, money_total_fee, money_fee, money_collection_fee, money_vat,
                kpi_ht, exchange_weight,
                receiver_province, receiver_district, receiver_ward, sort_code,
                request_payload, response_payload,
                created_at, updated_at
            FROM retail.orders
            WHERE tenant_id = ? AND id = ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new OrderResponse(
            rs.getObject("id", UUID.class),
            rs.getObject("tenant_id", UUID.class),
            rs.getString("order_code"),
            OrderStatus.valueOf(rs.getString("status")), 
            rs.getLong("shipping_providers_id"),
            rs.getBigDecimal("total_amount"),
            rs.getString("product_name"),

            rs.getString("receiver_name"),
            rs.getString("receiver_phone"),
            rs.getString("receiver_address"),
            
            rs.getString("sender_full_name"),
            rs.getString("sender_phone"),
            rs.getString("sender_address"),
            
            rs.getString("vtp_service_id"),
            rs.getString("vtp_order_number"),
            rs.getObject("vtp_order_status", Integer.class),
            
            rs.getObject("money_collection", Long.class),
            rs.getObject("money_total", Long.class),
            rs.getObject("money_total_fee", Long.class),
            rs.getObject("money_fee", Long.class),
            rs.getObject("money_collection_fee", Long.class),
            rs.getObject("money_vat", Long.class),
            rs.getObject("kpi_ht", Double.class),
            rs.getObject("exchange_weight", Long.class),
            
            rs.getObject("receiver_province", Long.class),
            rs.getObject("receiver_district", Long.class),
            rs.getObject("receiver_ward", Long.class),
            rs.getString("sort_code"),
            
            rs.getString("request_payload"),
            rs.getString("response_payload"),
            
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        ), tenantId, orderId).stream().findFirst();
    }

    public int updateVtpOrderInfo(
            UUID tenantId,
            UUID orderId,
            String vtpOrderNumber,
            String vtpServiceId,
            Long moneyTotal,
            Long moneyTotalFee,
            Long moneyFee,
            Long moneyCollectionFee,
            Long moneyVat,
            Long exchangeWeight,
            String sortCode,
            String requestPayload,
            String responsePayload,
            String status
    ) {
        String sql = """
            UPDATE retail.orders
            SET vtp_order_number = ?,
                vtp_service_id = ?,
                money_total = ?,
                money_total_fee = ?,
                money_fee = ?,
                money_collection_fee = ?,
                money_vat = ?,
                exchange_weight = ?,
                sort_code = ?,
                request_payload = ?::jsonb,
                response_payload = ?::jsonb,
                status = ?,
                updated_at = NOW()
            WHERE tenant_id = ?
            AND id = ?
            """;

        return jdbcTemplate.update(
                sql,
                vtpOrderNumber,
                vtpServiceId,
                moneyTotal,
                moneyTotalFee,
                moneyFee,
                moneyCollectionFee,
                moneyVat,
                exchangeWeight,
                sortCode,
                requestPayload,
                responsePayload,
                status,
                tenantId,
                orderId
        );
    }

    public Optional<OrderResponse> findByVtpOrderNumber(String vtpOrderNumber) {
        String sql = """
            SELECT 
                id, tenant_id, order_code, status, shipping_providers_id, total_amount, product_name,
                receiver_name, receiver_phone, receiver_address,
                sender_full_name, sender_phone, sender_address,
                vtp_service_id, vtp_order_number, vtp_order_status, 
                money_collection, money_total, money_total_fee, money_fee, money_collection_fee, money_vat,
                kpi_ht, exchange_weight,
                receiver_province, receiver_district, receiver_ward, sort_code,
                request_payload, response_payload,
                created_at, updated_at
            FROM retail.orders
            WHERE vtp_order_number = ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new OrderResponse(
            rs.getObject("id", UUID.class),
            rs.getObject("tenant_id", UUID.class),
            rs.getString("order_code"),
            OrderStatus.valueOf(rs.getString("status")), 
            rs.getLong("shipping_providers_id"),
            rs.getBigDecimal("total_amount"),
            rs.getString("product_name"),

            rs.getString("receiver_name"),    
            rs.getString("receiver_phone"),   
            rs.getString("receiver_address"), 
            
            rs.getString("sender_full_name"),
            rs.getString("sender_phone"),
            rs.getString("sender_address"),
            
            rs.getString("vtp_service_id"),
            rs.getString("vtp_order_number"),
            rs.getObject("vtp_order_status", Integer.class), 
            
            rs.getObject("money_collection", Long.class),
            rs.getObject("money_total", Long.class),
            rs.getObject("money_total_fee", Long.class),
            rs.getObject("money_fee", Long.class),
            rs.getObject("money_collection_fee", Long.class),
            rs.getObject("money_vat", Long.class),
            rs.getObject("kpi_ht", Double.class),
            rs.getObject("exchange_weight", Long.class),
            
            rs.getObject("receiver_province", Long.class),
            rs.getObject("receiver_district", Long.class),
            rs.getObject("receiver_ward", Long.class),
            rs.getString("sort_code"),
            
            rs.getString("request_payload"),
            rs.getString("response_payload"),
            
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        ), vtpOrderNumber).stream().findFirst();
    }

    public void updateWebhookInfo(
            String vtpOrderNumber, 
            Integer vtpOrderStatus, 
            String webhookPayload,
            OrderStatus systemStatus 
    ) {
        String sql = """
            UPDATE retail.orders
            SET vtp_order_status = ?,
                vtp_web_hook_response = ?::jsonb,
                status = ?,
                updated_at = NOW()
            WHERE vtp_order_number = ?
            """;

        jdbcTemplate.update(sql, 
                vtpOrderStatus, 
                webhookPayload, 
                systemStatus.name(), 
                vtpOrderNumber
        );
    }

    public boolean existsByVtpOrderNumber(String vtpOrderNumber) {
        String sql = "SELECT EXISTS(SELECT 1 FROM retail.orders WHERE vtp_order_number = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, vtpOrderNumber);
        return Boolean.TRUE.equals(exists);
    }

    public List<OrderSummaryResponse> getAll(UUID tenantId, int page, int size) {
        String sql = """
            SELECT 
                id, order_code, status, receiver_name, receiver_phone, total_amount, vtp_order_number, created_at
            FROM retail.orders
            WHERE tenant_id = ?
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
            """;

        int offset = (page - 1) * size;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new OrderSummaryResponse(
            rs.getObject("id", UUID.class),
            rs.getString("order_code"),
            rs.getString("status"),
            rs.getString("receiver_name"),
            rs.getString("receiver_phone"),
            rs.getBigDecimal("total_amount"),
            rs.getString("vtp_order_number"),
            rs.getTimestamp("created_at")
        ), tenantId, size, offset);
    }

    public long countAll(UUID tenantId) {
        String sql = "SELECT COUNT(*) FROM retail.orders WHERE tenant_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tenantId);
        return count != null ? count : 0L;
    }
}