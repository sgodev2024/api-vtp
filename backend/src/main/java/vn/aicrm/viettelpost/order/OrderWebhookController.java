package vn.aicrm.viettelpost.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.aicrm.viettelpost.order.dto.ViettelPostWebHookRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders/webhook")
public class OrderWebhookController {

    private final OrderService orderService;

    @Value("${viettelpost.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/vtp")
    public ResponseEntity<String> receiveViettelPostWebhook(@RequestBody ViettelPostWebHookRequest request) {
        String receivedToken = request.token();

        // 1. Xác thực Token an toàn (Nếu sai thì chặn luôn)
        if (receivedToken == null || !webhookSecret.equals(receivedToken)) {
            log.warn("Cảnh báo: Webhook Token không hợp lệ. Token nhận được: {}", receivedToken);
            // Vẫn trả về 200 để VTP không retry, nhưng không xử lý nghiệp vụ
            return ResponseEntity.ok("Invalid Token - Ignored"); 
        }

        try {
            orderService.processViettelPostWebhook(request);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            log.error("Lỗi xử lý webhook VTP: ", e);
            return ResponseEntity.ok("Error Handled"); 
        }
    }
}