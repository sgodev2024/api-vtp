package vn.aicrm.viettelpost.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetViettelPostServiceRequest(
    @JsonProperty("SENDER_ADDRESS")
    String senderAddress,

    @JsonProperty("RECEIVER_ADDRESS")
    String receiverAddress,

    @JsonProperty("PRODUCT_TYPE")
    String productType,

    @JsonProperty("PRODUCT_WEIGHT")
    Long productWeight,

    @JsonProperty("TYPE")
    Long type
) {
    
}
