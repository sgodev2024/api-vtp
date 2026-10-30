package vn.aicrm.viettelpost.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BaseOrderResponse(    
    UUID id,
    String orderCode,
    String status,
    String senderName,
    String senderPhone,

    String senderAddress,
    String receiverName,
    String receiverPhone,
    String receiverAddress,

    String productName,
    
    BigDecimal totalPrice,
    BigDecimal moneyCollection
) {
    
}
