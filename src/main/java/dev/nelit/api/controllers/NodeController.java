package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.node.CreateNode;
import dev.nelit.api.dto.request.node.RegisterNode;
import dev.nelit.api.dto.request.node.UpdateNode;
import dev.nelit.api.dto.response.LocationResponse;
import dev.nelit.api.dto.response.node.NodeResponse;
import dev.nelit.api.dto.response.node.NodeTokenResponse;
import dev.nelit.api.dto.response.node.RegisterResponse;
import dev.nelit.api.services.EnrollmentTokenService;
import dev.nelit.api.services.NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Tag(name = "Nodes", description = "Physical/virtual node management, PKI enrollment and mTLS registration")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/nodes")
public class NodeController {

    private final NodeService nodeService;
    private final EnrollmentTokenService tokenService;

    @Operation(
        summary = "Get available locations",
        description = "Returns a list of available locations for VPS deployment",
        responses = {
            @ApiResponse(responseCode = "200", description = "Locations returned successfully",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocationResponse.class))))
        }
    )
    @GetMapping("/locations")
    public Flux<LocationResponse> getLocations() {
        return nodeService.getLocations();
    }

    @Operation(
        summary = "Create a node (Admin)",
        description = "Adds a new hosting node to the infrastructure. Requires admin privileges. " +
            "The node created here has no certificate yet — it must complete PKI enrollment " +
            "(see /{node_id}/enrollment-token and /register) before it can be reached over mTLS.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Node registered successfully",
                content = @Content(schema = @Schema(implementation = NodeResponse.class))),
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
    public Mono<NodeResponse> create(@RequestBody CreateNode createNodeDTO) {
        return nodeService.create(createNodeDTO);
    }

    @Operation(
        summary = "Update a node (Admin)",
        description = "Updates configuration of an existing node by ID. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Node updated successfully",
                content = @Content(schema = @Schema(implementation = NodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Node not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PatchMapping("/{node_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NodeResponse> update(@RequestBody UpdateNode updateNodeDTO, @PathVariable("node_id") Long nodeId) {
        return nodeService.update(nodeId, updateNodeDTO);
    }

    @Operation(
        summary = "Delete a node (Admin)",
        description = "Permanently removes a node from the infrastructure. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Node deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Node not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @DeleteMapping("/{node_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("node_id") Long nodeId) {
        return nodeService.delete(nodeId);
    }

    @Operation(
        summary = "Enrollment token (Admin)",
        description = "Generates a one-time enrollment token for a node. Requires admin privileges.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Token issued successfully",
                content = @Content(schema = @Schema(implementation = NodeTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Node not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/{node_id}/enrollment-token")
    public Mono<NodeTokenResponse> issueToken(@PathVariable("node_id") Long nodeId) {
        Duration ttl = Duration.ofMinutes(20);
        return tokenService.issueToken(nodeId, ttl).map(token -> new NodeTokenResponse(token, ttl.toSeconds()));
    }

    @Operation(
        summary = "Register node",
        description = "Exchanges an enrollment token and CSR for a signed node certificate.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Certificate issued successfully",
                content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Malformed CSR or signing failure",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Token invalid, expired, or already used",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Node not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/register")
    public Mono<RegisterResponse> register(@RequestBody RegisterNode registerNode) {
        return nodeService.signAndPersist(registerNode);
    }
}