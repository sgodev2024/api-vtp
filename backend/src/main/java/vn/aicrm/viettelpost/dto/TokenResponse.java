package vn.aicrm.viettelpost.dto;

import java.time.OffsetDateTime;

public record TokenResponse(
    String token,
    OffsetDateTime expiresAt
) {
    
}
