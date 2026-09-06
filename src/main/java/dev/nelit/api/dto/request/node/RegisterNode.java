package dev.nelit.api.dto.request.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterNode(
    String token,
    @JsonProperty("csr_pem") String csrPem
) {}