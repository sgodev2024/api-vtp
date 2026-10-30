package vn.aicrm.viettelpost.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ViettelPostWebHookRequest(
    @JsonProperty("DATA")
    Data data,

    @JsonProperty("TOKEN")
    String token
) {
    public record Data(
        @JsonProperty("ORDER_NUMBER")
        String orderNumber,

        @JsonProperty("ORDER_REFERENCE")
        String orderReference,

        @JsonProperty("ORDER_STATUSDATE")
        String orderStatusDate,

        @JsonProperty("ORDER_STATUS")
        Integer orderStatus,

        @JsonProperty("STATUS_NAME")
        String statusName,

        @JsonProperty("LOCALION_CURRENTLY")
        String locationCurrently,

        @JsonProperty("NOTE")
        String note,

        @JsonProperty("MONEY_COLLECTION")
        Long moneyCollection,

        @JsonProperty("MONEY_FEECOD")
        Long moneyFeeCod,

        @JsonProperty("MONEY_TOTALFEE")
        Long moneyTotalFee,

        @JsonProperty("MONEY_TOTAL")
        Long moneyTotal,

        @JsonProperty("EXPECTED_DELIVERY")
        String expectedDelivery,

        @JsonProperty("PRODUCT_WEIGHT")
        Long productWeight,

        @JsonProperty("ORDER_SERVICE")
        String orderService,

        @JsonProperty("ORDER_PAYMENT")
        Integer orderPayment,

        @JsonProperty("EXPECTED_DELIVERY_DATE")
        String expectedDeliveryDate,

        @JsonProperty("DETAIL")
        List<Detail> detail,

        @JsonProperty("VOUCHER_VALUE")
        Long voucherValue,

        @JsonProperty("MONEY_COLLECTION_ORIGIN")
        Long moneyCollectionOrigin,

        @JsonProperty("EMPLOYEE_NAME")
        String employeeName,

        @JsonProperty("EMPLOYEE_PHONE")
        String employeePhone,

        @JsonProperty("IS_RETURNING")
        Boolean isReturning,

        @JsonProperty("REASON_CODE")
        String reasonCode
    ) {}

    public record Detail(
        @JsonProperty("CODE")
        String code,

        @JsonProperty("VALUE")
        Object value
    ) {}
}