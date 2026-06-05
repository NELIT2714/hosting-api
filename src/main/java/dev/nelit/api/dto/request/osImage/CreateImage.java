package dev.nelit.api.dto.request.osImage;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateImage(
    @JsonProperty("image_name") @NotBlank String imageName,
    @JsonProperty("file_name") @NotBlank String fileName
) {}
