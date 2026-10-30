package vn.aicrm.viettelpost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddShippingProviderRequest(
        @NotBlank
        @Size(max = 50)
        String code,

        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Size(max = 100)
        String senderName,

        @NotBlank
        @Size(max = 50)
        String senderPhone,

        @NotBlank
        @Size(max = 500)
        String senderAddress
) {
}