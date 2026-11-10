package vn.aicrm.viettelpost.order.viettelpost;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.aicrm.exception.ViettelPostApiException;
import vn.aicrm.viettelpost.ViettelPostTokenService;
import vn.aicrm.viettelpost.dto.TokenResponse;
import vn.aicrm.viettelpost.order.dto.GetViettelPostServiceRequest;
import vn.aicrm.viettelpost.order.dto.GetViettelPostServiceResponse;
import vn.aicrm.viettelpost.order.dto.ViettelPostOrderRequest;
import vn.aicrm.viettelpost.order.dto.ViettelPostOrderResponse;
import vn.aicrm.viettelpost.order.dto.ViettelPostResponse;
import vn.coreplatform.kernel.TenantContext;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViettelPostService {

    private final ViettelPostTokenService viettelPostTokenService;
    private final RestClient viettelPostRestClient;
    

    public ViettelPostResponse<ViettelPostOrderResponse> createViettelPostOrder(Long providerId, ViettelPostOrderRequest request){
        
        UUID tenantId = TenantContext.current();
        TokenResponse tokenResponse = viettelPostTokenService.getToken(providerId, tenantId);
        if(tokenResponse.expiresAt() == null || !tokenResponse.expiresAt().isAfter(OffsetDateTime.now())){
            throw new ViettelPostApiException("Token ViettelPost đã hết hạn hoặc trống");
        }

        String token = tokenResponse.token();
        
        try{

            log.info("Đang gọi api VTP...");
            ViettelPostResponse<ViettelPostOrderResponse> response =  viettelPostRestClient.post()
                    .uri("/v2/order/createOrderNlp")
                    .header("Content-Type", "application/json")
                    .header("Token", token)
                    .header("Cookie", "SERVERID=A")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ViettelPostResponse<ViettelPostOrderResponse>>() {
                        
                    });
            log.info("Response: " + response);
            return response;
        } catch (Exception e) {
            log.error("Lỗi khi gọi API lấy dịch vụ Viettel Post: {}", e.getMessage(), e);
            throw new ViettelPostApiException("Không thể lấy danh sách dịch vụ từ Viettel Post", e);
        }
    }
    
    public GetViettelPostServiceResponse getViettelPostService(Long shippingProviderId, GetViettelPostServiceRequest request){
        UUID tenantId = TenantContext.current();
        TokenResponse tokenResponse = viettelPostTokenService.getToken(shippingProviderId, tenantId);
        if(tokenResponse.expiresAt() == null || !tokenResponse.expiresAt().isAfter(OffsetDateTime.now())){
            throw new ViettelPostApiException("Token ViettelPost đã hết hạn hoặc trống");
        }

        String token = tokenResponse.token();

        try{
            log.info("Đang gọi api VTP...");
            GetViettelPostServiceResponse response = viettelPostRestClient.post()
                    .uri("/v2/order/getPriceAllNlp")
                    .header("Content-Type", "application/json")
                    .header("Token", token)
                    .header("Cookie", "SERVERID=A")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<GetViettelPostServiceResponse>() {});

            log.info("Response: " + response);
            return response;
        } catch(RestClientException e){
            log.error("Lỗi khi gọi API lấy dịch vụ Viettel Post: {}", e.getMessage(), e);
            throw new ViettelPostApiException("Không thể lấy danh sách dịch vụ từ Viettel Post", e);
        }
    }
}