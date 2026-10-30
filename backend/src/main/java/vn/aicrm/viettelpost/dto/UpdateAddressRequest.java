package vn.aicrm.viettelpost.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAddressRequest(
    @NotBlank
    String senderName,
    @NotBlank
    String senderPhone,
    @NotBlank
    String senderAddress
) {
    
}
