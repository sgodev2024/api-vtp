package vn.aicrm.viettelpost.order.viettelpost;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ViettelPostRestClientConfig {

    @Value("${viettelpost.api.base-url}")
    private String baseUrl;
    
    @Bean
    public RestClient viettelPostRestClient(){
        return RestClient.builder()
                .baseUrl(baseUrl) // Hoặc partner.viettelpost.vn khi lên Production
                .defaultHeader("Content-Type", "application/json;charset=UTF-8")
                .build();
    }


}
