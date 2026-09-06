package dev.nelit.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pki")
public record PkiProperties(String apiCert, String apiKey, String caCert, String caKey, int nodeCertValidityDays) {}
