package vn.aicrm.viettelpost.dto;

import java.sql.Timestamp;

public record ShippingProviderResponse(
    Long id,
    String code,
    String name,
    String token,
    boolean isEnable,
    String senderName,
    String senderPhone,
    String senderAddress,
    Timestamp token_expires_at

) {}
