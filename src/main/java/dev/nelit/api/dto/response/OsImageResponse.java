package dev.nelit.api.dto.response;

public record OsImageResponse(
    Long idOsImage,
    String imageName,
    String fileName
) {}
