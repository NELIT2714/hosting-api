package dev.nelit.api.services.impl;

import com.stripe.service.issuing.TokenService;
import dev.nelit.api.controllers.NodeController;
import dev.nelit.api.domain.entity.Node;
import dev.nelit.api.domain.exception.node.FailedToSignCsrException;
import dev.nelit.api.domain.exception.node.InvalidTokenException;
import dev.nelit.api.domain.exception.node.NodeNameAlreadyTakenException;
import dev.nelit.api.domain.exception.node.NodeNotFoundException;
import dev.nelit.api.dto.request.node.CreateNode;
import dev.nelit.api.dto.request.node.RegisterNode;
import dev.nelit.api.dto.request.node.UpdateNode;
import dev.nelit.api.dto.response.LocationResponse;
import dev.nelit.api.dto.response.node.NodeResponse;
import dev.nelit.api.dto.response.node.RegisterResponse;
import dev.nelit.api.mappers.NodeMapper;
import dev.nelit.api.pki.CertificateAuthority;
import dev.nelit.api.repository.NodeRepository;
import dev.nelit.api.services.EnrollmentTokenService;
import dev.nelit.api.services.NodeService;
import dev.nelit.api.util.PemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final NodeMapper nodeMapper;
    private final CertificateAuthority ca;
    private final EnrollmentTokenService tokenService;

    @Override
    public Flux<NodeResponse> getAll() {
        return nodeRepository.findAllByIsActiveIsTrue().map(nodeMapper::toResponse);
    }

    @Override
    public Mono<NodeResponse> getById(Long idNode) {
        return nodeRepository.findByIdNode(idNode)
            .switchIfEmpty(Mono.error(new NodeNotFoundException()));
    }

    @Override
    public Flux<LocationResponse> getLocations() {
        return nodeRepository.findAllByIsActiveIsTrue()
            .map(node -> new LocationResponse(node.getIdNode(), node.getLocation()));
    }

    @Override
    public Mono<NodeResponse> create(CreateNode createNodeDTO) {
        Node node = Node.builder()
            .nodeName(createNodeDTO.nodeName())
            .ipAddress(createNodeDTO.ipAddress())
            .grpcPort(createNodeDTO.grpcPort())
            .location(createNodeDTO.location())
            .isActive(createNodeDTO.isActive())
            .build();

        return nodeRepository.save(node)
            .map(nodeMapper::toResponse)
            .onErrorMap(DuplicateKeyException.class, _ -> new NodeNameAlreadyTakenException());
    }

    @Override
    public Mono<NodeResponse> update(Long idNode, UpdateNode updateNodeDTO) {
        return nodeRepository.findById(idNode)
            .switchIfEmpty(Mono.error(new NodeNotFoundException()))
            .flatMap(node -> {
                nodeMapper.update(updateNodeDTO, node);
                return nodeRepository.save(node);
            })
            .map(nodeMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(Long idNode) {
        return nodeRepository.findById(idNode)
            .switchIfEmpty(Mono.error(new NodeNotFoundException()))
            .flatMap(nodeRepository::delete);
    }

    @Override
    public Mono<RegisterResponse> signAndPersist(RegisterNode registerNode) {
        return tokenService.consumeToken(registerNode.token())
            .switchIfEmpty(Mono.error(new InvalidTokenException()))
            .flatMap(nodeId -> nodeRepository.findById(nodeId)
                .switchIfEmpty(Mono.error(new NodeNotFoundException()))
                .flatMap(node -> {
                    try {
                        X509Certificate cert = ca.signNodeCsr(registerNode.csrPem(), node.getIdNode());
                        String fingerprint = sha256Fingerprint(cert);

                        node.setCertFingerprint(fingerprint);
                        node.setCertIssuedAt(cert.getNotBefore().toInstant());
                        node.setCertExpiresAt(cert.getNotAfter().toInstant());

                        String certPem = PemUtils.toPem(cert);
                        String caCertPem = PemUtils.toPem(ca.caCertificate());

                        return nodeRepository.save(node).map(_ -> new RegisterResponse(certPem, caCertPem));
                    } catch (Exception e) {
                        return Mono.error(new FailedToSignCsrException(e.getMessage()));
                    }
                })
            );
    }

    private String sha256Fingerprint(X509Certificate cert) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(digest.digest(cert.getEncoded()));
    }
}
