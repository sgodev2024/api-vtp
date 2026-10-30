package vn.aicrm.viettelpost.order.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ViettelPostWebhookResponse(
    UUID id,
    UUID orderId,
    UUID tenantId,

    String orderNumber,
    String orderReference,
    LocalDateTime orderStatusDate,
    int orderStatus,
    String statusName,
    String location_currently,
    String note,
    Long mon
) {

}
