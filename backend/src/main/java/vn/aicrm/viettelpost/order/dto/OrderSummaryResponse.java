package vn.aicrm.viettelpost.order.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record OrderSummaryResponse(
    UUID id,
    String orderCode,
    String status,
    String receiverName,
    String receiverPhone,
    BigDecimal totalAmount,
    String vtpOrderNumber,
    Timestamp createdAt
) {}