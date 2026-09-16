package dev.nelit.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "payments.nowpayments")
public class NowPaymentsProperties {
    private String apiKey;
    private String ipnSecret;
    private String baseUrl;
    private String successUrl;
    private String cancelUrl;
    private String ipnCallbackUrl;
}
