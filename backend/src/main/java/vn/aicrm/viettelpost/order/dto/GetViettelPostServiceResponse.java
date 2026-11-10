
package vn.aicrm.viettelpost.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetViettelPostServiceResponse(

        @JsonProperty("SENDER_ADDRESS")
        SenderAddress senderAddress,

        @JsonProperty("RECEIVER_ADDRESS")
        ReceiverAddress receiverAddress,

        @JsonProperty("RESULT")
        List<ServiceResult> result

) {

    /**
     * Địa chỉ người gửi
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SenderAddress(

            @JsonProperty("PROVINCE_ID")
            Long provinceId,

            @JsonProperty("DISTRICT_ID")
            Long districtId,

            @JsonProperty("WARD_ID")
            Long wardId,

            @JsonProperty("ADDRESS")
            String address

    ) {}

    /**
     * Địa chỉ người nhận
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ReceiverAddress(

            @JsonProperty("PROVINCE_ID")
            Long provinceId,

            @JsonProperty("DISTRICT_ID")
            Long districtId,

            @JsonProperty("WARD_ID")
            Long wardId,

            @JsonProperty("ADDRESS")
            String address

    ) {}

    /**
     * Chi tiết dịch vụ vận chuyển
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ServiceResult(

            @JsonProperty("MA_DV_CHINH")
            String maDvChinh,

            @JsonProperty("TEN_DICHVU")
            String tenDichVu,

            @JsonProperty("GIA_CUOC")
            Long giaCuoc,

            @JsonProperty("THOI_GIAN")
            String thoiGian,

            @JsonProperty("EXCHANGE_WEIGHT")
            Long exchangeWeight,

            @JsonProperty("EXTRA_SERVICE")
            List<ExtraService> extraService

    ) {

        /**
         * Dịch vụ cộng thêm
         */
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record ExtraService(

                @JsonProperty("SERVICE_CODE")
                String serviceCode,

                @JsonProperty("SERVICE_NAME")
                String serviceName,

                @JsonProperty("DESCRIPTION")
                String description

        ) {}
    }
}
