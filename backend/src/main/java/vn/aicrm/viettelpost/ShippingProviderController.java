package vn.aicrm.viettelpost;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.aicrm.viettelpost.dto.AddShippingProviderRequest;
import vn.aicrm.viettelpost.dto.ShippingProviderResponse;
import vn.aicrm.viettelpost.dto.UpdateAddressRequest;
import vn.aicrm.viettelpost.dto.UpdateShippingProviderStatusRequest;
import vn.aicrm.viettelpost.dto.UpdateTokenRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shipping-providers")
public class ShippingProviderController {

    private final ShippingProviderService shippingProviderService;
    private final ViettelPostTokenService viettelPostTokenService;

    @PostMapping
    public ResponseEntity<ShippingProviderResponse> addShippingProvider(@RequestBody AddShippingProviderRequest request) {
        return ResponseEntity.ok(shippingProviderService.addShippingProvider(request));
    }

    @PutMapping("/{id}/token")
    public ResponseEntity<Void> insertToken(@PathVariable Long id, @RequestBody UpdateTokenRequest request) {
        viettelPostTokenService.insertToken(request.token(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateShippingProviderStatusRequest request) {
        shippingProviderService.updateEnabled(id, request.isEnabled());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ShippingProviderResponse>> getAll(){
        return ResponseEntity.ok(shippingProviderService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ShippingProviderResponse> getProvider(@PathVariable Long id){
        return ResponseEntity.ok(shippingProviderService.findById(id));
    }

    @PutMapping("/{id}/address")
    public ResponseEntity<Integer> updateSenderInfo(@RequestBody @Valid UpdateAddressRequest request, @PathVariable Long id){
        return ResponseEntity.ok(shippingProviderService.updateSenderInfo(request, id));
    }
}