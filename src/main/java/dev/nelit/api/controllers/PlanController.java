package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.plan.CreatePlan;
import dev.nelit.api.dto.request.plan.UpdatePlan;
import dev.nelit.api.dto.response.PlanResponse;
import dev.nelit.api.services.PlanService;
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

@Tag(name = "Plans (Admin)", description = "VPS plan management — admin access only")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plans")
public class PlanController {

    private final PlanService planService;

    @Operation(
        summary = "Create a plan",
        description = "Creates a new VPS pricing plan. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Plan created successfully",
                content = @Content(schema = @Schema(implementation = PlanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PlanResponse> create(@RequestBody @Valid CreatePlan createPlanDTO) {
        return planService.create(createPlanDTO);
    }

    @Operation(
        summary = "Update a plan",
        description = "Updates an existing VPS plan by ID. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Plan updated successfully",
                content = @Content(schema = @Schema(implementation = PlanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Plan not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PatchMapping("/{plan_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PlanResponse> update(@RequestBody @Valid UpdatePlan updatePlanDTO, @PathVariable("plan_id") Long planId) {
        return planService.update(planId, updatePlanDTO);
    }

    @Operation(
        summary = "Delete a plan",
        description = "Permanently deletes a VPS plan by ID. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Plan deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Plan not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @DeleteMapping("/{plan_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("plan_id") Long planId) {
        return planService.delete(planId);
    }

}
