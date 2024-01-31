package com.skyme.apiclientsdk;

import com.skyme.apiclientsdk.client.ApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("skapi.client")
@Configuration
@Data
@ComponentScan
public class ApiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public ApiClient apiClient(){
        return new ApiClient(accessKey,secretKey);
    }
}
