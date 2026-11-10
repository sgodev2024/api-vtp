package vn.aicrm.viettelpost.order.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull
    @JsonProperty("receiveFullName") // Ép Jackson map đúng key này
    String receiveFullName,
    
    @NotNull
    @JsonProperty("receiverPhone")
    String receiverPhone,

    @NotNull
    @JsonProperty("receiverAddress")
    String receiverAddress,

    @JsonProperty("productName")
    String productName,
    
    @NotNull
    @JsonProperty("totalPrice")
    BigDecimal totalPrice,

    @JsonProperty("moneyCollection")
    BigDecimal moneyCollection
) {
    
}