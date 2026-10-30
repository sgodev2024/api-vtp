package vn.aicrm.viettelpost.order.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull
    String receiveFullName,
    @NotNull
    String receiverPhone,
    @NotNull
    String receiverAddress,
    String productName,
    Integer quantity,
    @NotNull
    BigDecimal totalPrice,
    BigDecimal moneyCollection,
    @NotNull
    Long  shippingProviderId
) {
    
}
