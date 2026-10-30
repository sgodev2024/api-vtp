package vn.aicrm.viettelpost.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViettelPostOrderRequest(
    
    @JsonProperty("ORDER_NUMBER")
    String orderNumber,

    @JsonProperty("SENDER_FULLNAME")
    String senderFullName,
    @JsonProperty("SENDER_PHONE")
    String senderPhone,
    @JsonProperty("SENDER_ADDRESS")
    String senderAddress,
    
    @JsonProperty("RECEIVER_FULLNAME")
    String receiverFullName,
    @JsonProperty("RECEIVER_PHONE")
    String receiverPhone,
    @JsonProperty("RECEIVER_ADDRESS")
    String receciverAddress,
    
    @JsonProperty("PRODUCT_NAME")
    String productName,

    @JsonProperty("MONEY_COLLECTION")
    Long moneyCollection,

    @JsonProperty("ORDER_PAYMENT")
    int orderPayment,
    @JsonProperty("ORDER_SERVICE")
    String orderService
) {
    
}
