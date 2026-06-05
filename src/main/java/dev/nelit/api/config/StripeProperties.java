package dev.nelit.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payments.stripe")
public class StripeProperties {
    private String secretKey;
    private String webhookSecret;
}
