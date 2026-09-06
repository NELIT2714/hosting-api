package dev.nelit.api.controllers;

import dev.nelit.api.domain.entity.Node;
import dev.nelit.api.domain.exception.node.NodeNotFoundException;
import dev.nelit.api.dto.request.node.CreateNode;
import dev.nelit.api.dto.request.node.RegisterNode;
import dev.nelit.api.dto.request.node.UpdateNode;
import dev.nelit.api.dto.response.LocationResponse;
import dev.nelit.api.dto.response.node.NodeResponse;
import dev.nelit.api.dto.response.node.NodeTokenResponse;
import dev.nelit.api.pki.CertificateAuthority;
import dev.nelit.api.repository.NodeRepository;
import dev.nelit.api.services.EnrollmentTokenService;
import dev.nelit.api.services.NodeService;
import dev.nelit.api.util.PemUtils;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Base64;

@Tag(name = "Nodes", description = "Physical/virtual node management, PKI enrollment and mTLS registration")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/nodes")
public class NodeController {

    private final NodeService nodeService;
    private final EnrollmentTokenService tokenService;
    private final CertificateAuthority ca;
    private final NodeRepository nodeRepository;

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
        return tokenService.issueToken(nodeId, ttl)
            .map(token -> new NodeTokenResponse(token, ttl.toSeconds()));
    }
    public record RegisterResponse(String certificatePem, String caCertificatePem) {}

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
    public Mono<RegisterResponse> register(@RequestBody RegisterNode req) {
        return tokenService.consumeToken(req.token())
            .switchIfEmpty(Mono.error(new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "invalid, expired, or already used token")))
            .flatMap(nodeId -> nodeRepository.findById(nodeId)
                .switchIfEmpty(Mono.error(new NodeNotFoundException()))
                .flatMap(node -> signAndPersist(node, req.csrPem())));
    }

    private Mono<RegisterResponse> signAndPersist(Node node, String csrPem) {
        try {
            X509Certificate cert = ca.signNodeCsr(csrPem, node.getIdNode());
            String fingerprint = sha256Fingerprint(cert);

            node.setCertFingerprint(fingerprint);
            node.setCertIssuedAt(cert.getNotBefore().toInstant());
            node.setCertExpiresAt(cert.getNotAfter().toInstant());

            String certPem = PemUtils.toPem(cert);
            String caCertPem = PemUtils.toPem(ca.caCertificate());

            return nodeRepository.save(node)
                .map(saved -> new RegisterResponse(certPem, caCertPem));
        } catch (Exception e) {
            return Mono.error(new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "failed to sign CSR: " + e.getMessage()));
        }
    }

    private String sha256Fingerprint(X509Certificate cert) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(digest.digest(cert.getEncoded()));
    }
}