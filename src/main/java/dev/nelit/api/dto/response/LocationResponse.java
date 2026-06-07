package dev.nelit.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocationResponse(@JsonProperty("id_location") Long idLocation, String location) {
}
