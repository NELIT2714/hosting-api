package dev.nelit.api.services;

import dev.nelit.api.dto.request.node.CreateNode;
import dev.nelit.api.dto.request.node.UpdateNode;
import dev.nelit.api.dto.response.NodeResponse;
import reactor.core.publisher.Mono;

public interface NodeService {
    Mono<NodeResponse> create(CreateNode createNodeDTO);
    Mono<NodeResponse> update(Long idNode, UpdateNode updateNodeDTO);
    Mono<Void> delete(Long idNode);
}
