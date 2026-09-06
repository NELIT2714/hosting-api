package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.Node;
import dev.nelit.api.domain.exception.node.NodeNameAlreadyTakenException;
import dev.nelit.api.domain.exception.node.NodeNotFoundException;
import dev.nelit.api.dto.request.node.CreateNode;
import dev.nelit.api.dto.request.node.UpdateNode;
import dev.nelit.api.dto.response.LocationResponse;
import dev.nelit.api.dto.response.node.NodeResponse;
import dev.nelit.api.mappers.NodeMapper;
import dev.nelit.api.repository.NodeRepository;
import dev.nelit.api.services.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final NodeMapper nodeMapper;

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
}
