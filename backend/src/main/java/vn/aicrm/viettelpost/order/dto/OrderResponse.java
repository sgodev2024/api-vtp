package vn.aicrm.viettelpost.order.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import vn.aicrm.enums.OrderStatus;

public record OrderResponse(
    UUID id,
    UUID tenantId,
    String orderCode,
    OrderStatus status,
    Long shippingProvidersId, 
    BigDecimal totalAmount, 
    String productName,

    String receiverName,
    String receiverPhone, 
    String receiverAddress,

    String senderFullName,
    String senderPhone,
    String senderAddress,


    String vtpServiceId,

    String vtpOrderNumber,

    Long moneyCollection,
    Long moneyTotal,
    Long moneyTotalFee,
    Long moneyFee,
    Long moneyCollectionFee, 
    Long moneyVAT, 
    Double kpiHt,
    Long exchangeWeight,
    Long receiverProvince,
    Long receiverDistrict,
    Long receiverWard,
    String sortCode,

    String requestPayload,
    String responsePayload, 

    Timestamp createdAt,
    Timestamp updatedAt
) {
    
}
