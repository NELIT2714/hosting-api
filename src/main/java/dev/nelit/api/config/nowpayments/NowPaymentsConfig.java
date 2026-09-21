package dev.nelit.api.config.nowpayments;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(NowPaymentsProperties.class)
@RequiredArgsConstructor
public class NowPaymentsConfig {

    private final NowPaymentsProperties properties;

    @Bean
    public WebClient nowPaymentsWebClient() {
        return WebClient.builder()
            .baseUrl(properties.getBaseUrl())
            .defaultHeader("x-api-key", properties.getApiKey())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
