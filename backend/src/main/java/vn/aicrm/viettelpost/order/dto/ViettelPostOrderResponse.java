package vn.aicrm.viettelpost.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViettelPostOrderResponse(

    @JsonProperty("ORDER_NUMBER")
    String orderNumber,

    @JsonProperty("MONEY_COLLECTION")
    Long moneyCollection,

    @JsonProperty("MONEY_TOTAL")
    Long moneyTotal,

    @JsonProperty("MONEY_TOTAL_FEE")
    Long moneyTotalFee,

    @JsonProperty("MONEY_FEE")
    Long moneyFee,

    @JsonProperty("MONEY_COLLECTION_FEE")
    Long moneyCollectionFee,

    @JsonProperty("MONEY_VAT")
    Long moneyVat,

    @JsonProperty("KPI_HT")
    Double kpiHt,

    @JsonProperty("EXCHANGE_WEIGHT")
    Long exchangeWeight,

    @JsonProperty("RECEIVER_PROVINCE")
    Long receiverProvince,

    @JsonProperty("RECEIVER_DISTRICT")
    Long receiverDistrict,

    @JsonProperty("RECEIVER_WARD")
    Long receiverWard,

    @JsonProperty("SORT_CODE")
    String sortCode

) {
}