package dev.nelit.api.dto.response.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterResponse(
    @JsonProperty("certificate_pem") String certificatePem,
    @JsonProperty("ca_certificate_pem") String caCertificatePem
) {
}
