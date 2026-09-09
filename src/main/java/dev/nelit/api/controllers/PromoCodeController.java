package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.promo.CreatePromoCode;
import dev.nelit.api.dto.request.promo.UpdatePromoCode;
import dev.nelit.api.dto.response.PromoCodeResponse;
import dev.nelit.api.services.promo.PromoCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Promo Codes", description = "Promo code management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/promo-codes")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @Operation(
        summary = "Create a promo code",
        description = "Creates a new promo code with the specified parameters (discount, usage limits, validity period, etc.).",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Promo code created successfully",
                content = @Content(schema = @Schema(implementation = PromoCodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Promo code with the given code already exists",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PromoCodeResponse> create(@Valid @RequestBody CreatePromoCode promoCodeDTO) {
        return promoCodeService.create(promoCodeDTO);
    }

    @Operation(
        summary = "Update a promo code",
        description = "Partially updates an existing promo code by ID. Only non-null fields in the request body are applied; omitted fields remain unchanged.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Promo code updated successfully",
                content = @Content(schema = @Schema(implementation = PromoCodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Promo code not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PatchMapping("/{promoId}")
    public Mono<PromoCodeResponse> update(
        @PathVariable Long promoId,
        @RequestBody UpdatePromoCode request
    ) {
        return promoCodeService.update(promoId, request);
    }

    @Operation(
        summary = "Delete a promo code",
        description = "Permanently deletes a promo code by ID.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Promo code deleted successfully",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Promo code not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @DeleteMapping("/{promo_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("promo_id") Long promoId) {
        return promoCodeService.delete(promoId);
    }
}